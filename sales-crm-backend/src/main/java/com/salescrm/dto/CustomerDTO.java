package com.salescrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class CustomerDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;
        @Email @NotBlank
        private String email;
        private String phone;
        private String company;
        private String address;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String phone;
        private String company;
        private String address;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String company;
        private String address;
        private Long convertedFromLeadId;
        private UserDTO.Response assignedTo;
        private LocalDateTime createdAt;
    }
}
