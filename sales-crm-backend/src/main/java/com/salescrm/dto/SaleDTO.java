package com.salescrm.dto;

import com.salescrm.enums.SaleStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleDTO {

    // ---- Item in a sale (create) ----
    @Data
    public static class SaleItemRequest {
        @NotNull
        private Long productId;
        @NotNull @Min(1)
        private Integer quantity;
    }

    // ---- Create sale request ----
    @Data
    public static class CreateRequest {
        @NotNull
        private Long customerId;
        @NotEmpty
        private List<SaleItemRequest> items;
        private String notes;
    }

    // ---- Update status ----
    @Data
    public static class UpdateStatusRequest {
        @NotNull
        private SaleStatus status;
    }

    // ---- Sale item response ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCategory;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    // ---- Full sale response ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private String customerCompany;
        private Long employeeId;
        private String employeeName;
        private List<SaleItemResponse> items;
        private BigDecimal totalAmount;
        private SaleStatus status;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // ---- Sales statistics (dashboard) ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesStats {
        private long totalSales;
        private long completedSales;
        private long pendingSales;
        private long cancelledSales;
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private BigDecimal weeklyRevenue;
        private List<TopEmployee> topEmployees;
        private List<TopProduct>  topProducts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopEmployee {
        private Long employeeId;
        private String employeeName;
        private BigDecimal totalSales;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private Long totalQuantity;
    }

    // ---- Employee sales summary ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeSalesSummary {
        private long totalSales;
        private BigDecimal totalRevenue;
        private long completedSales;
        private long pendingSales;
    }
}
