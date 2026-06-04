package com.example.inventory_service.event;

import java.math.BigDecimal;

public record OrderItemEvent(
    Long productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {}