package com.example.inventory_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Long sellerId,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}