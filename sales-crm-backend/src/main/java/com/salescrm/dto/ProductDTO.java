package com.salescrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;
        private String description;
        @NotNull @Positive
        private BigDecimal price;
        private String category;
        private Integer stock;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private Integer stock;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private Integer stock;
        private boolean active;
        private LocalDateTime createdAt;
    }
}
