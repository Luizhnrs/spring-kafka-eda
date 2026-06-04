package com.lab.order_service.event;

public record OrderStatusChangedEvent(
    Long orderId,
    String status,
    String reason
) {}
