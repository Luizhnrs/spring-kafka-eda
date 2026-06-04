package com.example.inventory_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private static final String PRODUCT_CREATED_TOPIC = "product-created";
    private static final String INVENTORY_EVENTS_TOPIC = "inventory-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publishing ProductCreatedEvent: {}", event);
        kafkaTemplate.send(PRODUCT_CREATED_TOPIC, event.productId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ProductCreatedEvent: {}", ex.getMessage(), ex);
                    } else {
                        log.info("ProductCreatedEvent published successfully: {}, offset: {}",
                                event, result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Publishing OrderStatusChangedEvent for orderId: {}, status: {}",
                event.orderId(), event.status());
        kafkaTemplate.send(INVENTORY_EVENTS_TOPIC, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish OrderStatusChangedEvent: {}", ex.getMessage(), ex);
                    } else {
                        log.info("OrderStatusChangedEvent published successfully for orderId: {}, offset: {}",
                                event.orderId(), result.getRecordMetadata().offset());
                    }
                });
    }
}
