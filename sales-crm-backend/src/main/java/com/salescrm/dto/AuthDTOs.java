package com.salescrm.dto;

import com.salescrm.enums.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

// ========== AUTH DTOs ==========
public class AuthDTOs {

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private RoleType role;

        public LoginResponse(String token, Long id, String name, String email, RoleType role) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }
}
