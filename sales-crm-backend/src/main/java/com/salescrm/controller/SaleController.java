package com.salescrm.controller;

import com.salescrm.dto.SaleDTO;
import com.salescrm.enums.SaleStatus;
import com.salescrm.security.UserDetailsImpl;
import com.salescrm.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired private SaleService saleService;

    // ── Admin: full access ──────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SaleDTO.Response>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<SaleDTO.SalesStats> getSalesStats() {
        return ResponseEntity.ok(saleService.getSalesStats());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EMPLOYEE')")
    public ResponseEntity<SaleDTO.Response> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.ok("Sale deleted");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<SaleDTO.Response> updateStatus(
            @PathVariable Long id,
            @RequestBody SaleDTO.UpdateStatusRequest request) {
        return ResponseEntity.ok(saleService.updateSaleStatus(id, request.getStatus()));
    }

    // ── Manager: view by employee ───────────────────────────

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(saleService.getSalesByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<SaleDTO.EmployeeSalesSummary> getEmployeeSummary(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(saleService.getEmployeeSummary(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<List<SaleDTO.Response>> getSalesByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(saleService.getSalesByCustomer(customerId));
    }

    // ── Employee: own sales ─────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_EMPLOYEE')")
    public ResponseEntity<SaleDTO.Response> createSale(
            @Valid @RequestBody SaleDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(saleService.createSale(request, userDetails.getId()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('SALES_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<SaleDTO.Response>> getMySales(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(saleService.getSalesByEmployee(userDetails.getId()));
    }

    @GetMapping("/my/summary")
    @PreAuthorize("hasAnyRole('SALES_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<SaleDTO.EmployeeSalesSummary> getMySummary(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(saleService.getEmployeeSummary(userDetails.getId()));
    }
}
