package com.salescrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class FollowUpDTO {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long leadId;
        @NotBlank
        private String notes;
        private LocalDateTime followUpDate;
        private String outcome;
    }

    @Data
    public static class Response {
        private Long id;
        private Long leadId;
        private String leadName;
        private UserDTO.Response createdBy;
        private String notes;
        private LocalDateTime followUpDate;
        private String outcome;
        private LocalDateTime createdAt;
        // Email notification status
        private boolean emailSent;
        private String emailStatus;
    }
}
