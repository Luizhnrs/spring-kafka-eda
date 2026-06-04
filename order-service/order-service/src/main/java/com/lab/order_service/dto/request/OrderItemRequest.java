package com.lab.order_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull(message = "productId is mandatory")
    Long productId,

    @NotNull(message = "quantity is mandatory")
    @Min(value = 1, message = "quantity must be at least 1")
    Integer quantity
) {}
