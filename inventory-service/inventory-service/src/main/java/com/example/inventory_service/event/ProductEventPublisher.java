package com.example.inventory_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private static final String TOPIC = "product-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publishing ProductCreatedEvent: {}", event);
        kafkaTemplate.send(TOPIC, event.productId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ProductCreatedEvent: {}", ex.getMessage(), ex);
                    } else {
                        log.info("ProductCreatedEvent published successfully: {}, offset: {}",
                                event, result.getRecordMetadata().offset());
                    }
                });
    }
}