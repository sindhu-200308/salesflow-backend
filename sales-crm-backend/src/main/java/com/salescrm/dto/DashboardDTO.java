package com.salescrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class DashboardDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AdminStats {
        private long totalLeads;
        private long newLeads;
        private long contactedLeads;
        private long interestedLeads;
        private long convertedLeads;
        private long rejectedLeads;
        private long totalCustomers;
        private long totalProducts;
        private long totalEmployees;
        private long totalManagers;
        // Sales additions
        private long totalSales;
        private BigDecimal totalRevenue;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ManagerStats {
        private long assignedLeads;
        private long newLeads;
        private long contactedLeads;
        private long interestedLeads;
        private long convertedLeads;
        private long rejectedLeads;
        private long totalEmployees;
        private long totalSales;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmployeeStats {
        private long assignedLeads;
        private long newLeads;
        private long contactedLeads;
        private long interestedLeads;
        private long convertedLeads;
        private long rejectedLeads;
        private long totalFollowUps;
        // Sales additions
        private long totalSales;
        private BigDecimal myRevenue;
    }
}
