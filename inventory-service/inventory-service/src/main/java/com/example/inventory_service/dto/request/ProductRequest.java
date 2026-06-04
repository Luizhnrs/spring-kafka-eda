package com.example.inventory_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    String name,

    @Size(max = 1000, message = "Description must have at most 1000 characters")
    String description,

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    BigDecimal price,

    @NotNull(message = "Stock is mandatory")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock
) {}