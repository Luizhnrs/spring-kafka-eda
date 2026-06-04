package com.lab.order_service.event;

import com.lab.order_service.entity.OrderStatus;
import com.lab.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "inventory-events", groupId = "order-service-group")
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Received OrderStatusChangedEvent for orderId: {}, status: {}, reason: {}",
                event.orderId(), event.status(), event.reason());

        OrderStatus status = OrderStatus.valueOf(event.status());
        orderService.updateStatus(event.orderId(), status);
    }
}
