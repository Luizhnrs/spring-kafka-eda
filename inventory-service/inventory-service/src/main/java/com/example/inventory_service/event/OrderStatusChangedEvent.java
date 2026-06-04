package com.example.inventory_service.event;

public record OrderStatusChangedEvent(
    Long orderId,
    String status
) {}