package com.example.inventory_service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductEventConsumer {

    @KafkaListener(topics = "product-created", groupId = "inventory-service-group")
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Received ProductCreatedEvent: {}", event);
        // Future: handle downstream logic (e.g., notify other services)
    }
}