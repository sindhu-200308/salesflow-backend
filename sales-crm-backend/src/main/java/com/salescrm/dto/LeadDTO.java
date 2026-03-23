package com.salescrm.dto;

import com.salescrm.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class LeadDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;
        @Email @NotBlank
        private String email;
        private String phone;
        private String company;
        private String source;
        private String notes;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String email;
        private String phone;
        private String company;
        private String source;
        private String notes;
        private LeadStatus status;
    }

    @Data
    public static class AssignRequest {
        private Long assignedToId;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String company;
        private String source;
        private String notes;
        private LeadStatus status;
        private UserDTO.Response assignedManager;
        private UserDTO.Response assignedEmployee;
        private UserDTO.Response createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
