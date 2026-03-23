package com.salescrm.service;

import com.salescrm.dto.SaleDTO;
import com.salescrm.entity.*;
import com.salescrm.enums.SaleStatus;
import com.salescrm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired private SaleRepository saleRepository;
    @Autowired private SaleItemRepository saleItemRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    // ─────────────────────────────────────────────────────────
    //  CREATE SALE
    // ─────────────────────────────────────────────────────────
    @Transactional
    public SaleDTO.Response createSale(SaleDTO.CreateRequest request, Long employeeId) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + request.getCustomerId()));
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // Build sale items and calculate total
        List<SaleItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SaleDTO.SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            SaleItem item = SaleItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            items.add(item);
            total = total.add(subtotal);
        }

        Sale sale = Sale.builder()
                .customer(customer)
                .employee(employee)
                .totalAmount(total)
                .status(SaleStatus.COMPLETED)
                .notes(request.getNotes())
                .build();

        Sale saved = saleRepository.save(sale);

        // Link items to saved sale
        for (SaleItem item : items) {
            item.setSale(saved);
        }
        saleItemRepository.saveAll(items);
        saved.setItems(items);

        return mapToResponse(saved);
    }

    // ─────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────
    public List<SaleDTO.Response> getAllSales() {
        return saleRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public SaleDTO.Response getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));
        return mapToResponse(sale);
    }

    public List<SaleDTO.Response> getSalesByEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return saleRepository.findByEmployeeOrderByCreatedAtDesc(employee)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<SaleDTO.Response> getSalesByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return saleRepository.findByCustomer(customer)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────
    //  UPDATE STATUS
    // ─────────────────────────────────────────────────────────
    public SaleDTO.Response updateSaleStatus(Long id, SaleStatus status) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));
        sale.setStatus(status);
        return mapToResponse(saleRepository.save(sale));
    }

    // ─────────────────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────────────────
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    //  STATISTICS
    // ─────────────────────────────────────────────────────────
    public SaleDTO.SalesStats getSalesStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek  = now.minusDays(now.getDayOfWeek().getValue() - 1L)
                                        .toLocalDate().atStartOfDay();

        // Top employees
        List<SaleDTO.TopEmployee> topEmployees = saleRepository.getTopEmployeesBySales()
                .stream().limit(5).map(row -> SaleDTO.TopEmployee.builder()
                        .employeeId(((User) row[0]).getId())
                        .employeeName(((User) row[0]).getName())
                        .totalSales((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());

        // Top products
        List<SaleDTO.TopProduct> topProducts = saleRepository.getTopProductsBySales()
                .stream().limit(5).map(row -> SaleDTO.TopProduct.builder()
                        .productId(((Product) row[0]).getId())
                        .productName(((Product) row[0]).getName())
                        .totalQuantity((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        return SaleDTO.SalesStats.builder()
                .totalSales(saleRepository.count())
                .completedSales(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .pendingSales(saleRepository.countByStatus(SaleStatus.PENDING))
                .cancelledSales(saleRepository.countByStatus(SaleStatus.CANCELLED))
                .totalRevenue(saleRepository.getTotalRevenue())
                .monthlyRevenue(saleRepository.getRevenueBetween(startOfMonth, now))
                .weeklyRevenue(saleRepository.getRevenueBetween(startOfWeek, now))
                .topEmployees(topEmployees)
                .topProducts(topProducts)
                .build();
    }

    public SaleDTO.EmployeeSalesSummary getEmployeeSummary(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        List<Sale> sales = saleRepository.findByEmployee(employee);
        BigDecimal revenue = saleRepository.getRevenueByEmployee(employee);
        long completed = sales.stream().filter(s -> s.getStatus() == SaleStatus.COMPLETED).count();
        long pending   = sales.stream().filter(s -> s.getStatus() == SaleStatus.PENDING).count();

        return SaleDTO.EmployeeSalesSummary.builder()
                .totalSales(sales.size())
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .completedSales(completed)
                .pendingSales(pending)
                .build();
    }

    // ─────────────────────────────────────────────────────────
    //  MAPPER
    // ─────────────────────────────────────────────────────────
    public SaleDTO.Response mapToResponse(Sale sale) {
        List<SaleDTO.SaleItemResponse> itemResponses = sale.getItems() == null
                ? List.of()
                : sale.getItems().stream().map(item -> SaleDTO.SaleItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productCategory(item.getProduct().getCategory())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return SaleDTO.Response.builder()
                .id(sale.getId())
                .customerId(sale.getCustomer().getId())
                .customerName(sale.getCustomer().getName())
                .customerEmail(sale.getCustomer().getEmail())
                .customerCompany(sale.getCustomer().getCompany())
                .employeeId(sale.getEmployee().getId())
                .employeeName(sale.getEmployee().getName())
                .items(itemResponses)
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus())
                .notes(sale.getNotes())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();
    }
}
