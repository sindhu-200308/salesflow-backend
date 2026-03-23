package com.salescrm.dto;

import com.salescrm.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class UserDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String phone;
        @NotNull
        private RoleType role;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String phone;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private RoleType role;
        private boolean active;
        private LocalDateTime createdAt;
    }
}
