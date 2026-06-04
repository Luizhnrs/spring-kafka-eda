package com.example.inventory_service.event;

import com.example.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId: {}, status: {}", event.orderId(), event.status());

        if ("CREATED".equals(event.status())) {
            try {
                productService.reserveStock(event);
                log.info("✅ Stock reserved successfully for orderId: {}", event.orderId());
            } catch (Exception e) {
                log.error("❌ Failed to reserve stock for orderId: {}: {}", event.orderId(), e.getMessage(), e);
                // Saga pattern: publicar evento de compensação aqui
            }
        } else if ("CANCELLED".equals(event.status()) || "PAYMENT_FAILED".equals(event.status())) {
            productService.restoreStock(event);
            log.info("🔄 Stock restored for orderId: {}", event.orderId());
        }
    }
}