package com.lab.order_service.event;

import java.math.BigDecimal;

public record OrderItemEvent(
    Long productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {}