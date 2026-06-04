package com.example.inventory_service.event;

import java.math.BigDecimal;

public record ProductCreatedEvent(
    Long productId,
    Long sellerId,
    String name,
    BigDecimal price
) {}