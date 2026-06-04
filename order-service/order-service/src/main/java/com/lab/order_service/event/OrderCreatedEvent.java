package com.lab.order_service.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
    Long orderId,
    Long userId,
    String status,
    BigDecimal totalAmount,
    List<OrderItemEvent> items
) {}