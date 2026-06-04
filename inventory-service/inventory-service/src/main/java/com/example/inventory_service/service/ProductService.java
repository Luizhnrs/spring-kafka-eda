package com.example.inventory_service.service;

import com.example.inventory_service.event.OrderCreatedEvent;
import com.example.inventory_service.event.OrderItemEvent;
import com.example.inventory_service.event.ProductCreatedEvent;
import com.example.inventory_service.dto.request.ProductRequest;
import com.example.inventory_service.dto.response.ProductResponse;
import com.example.inventory_service.entity.Product;
import com.example.inventory_service.event.ProductEventPublisher;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public ProductResponse create(ProductRequest request, Long sellerId) {
        log.info("Creating product for sellerId: {}", sellerId);

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .sellerId(sellerId)
                .isActive(true)
                .build();

        product = productRepository.save(product);
        log.info("Product created with id: {}", product.getId());

        // Publish Kafka event
        ProductCreatedEvent event = new ProductCreatedEvent(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getPrice());
        eventPublisher.publishProductCreated(event);

        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toResponse(product);
    }

    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
        for (OrderItemEvent item : event.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.productId()));

            if (product.getStock() < item.quantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product " + product.getName() +
                        ". Available: " + product.getStock() + ", requested: " + item.quantity());
            }

            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);
            log.info("Reserved {} units of product {} (remaining stock: {})",
                    item.quantity(), product.getName(), product.getStock());
        }
    }

    @Transactional
    public void restoreStock(OrderCreatedEvent event) {
        for (OrderItemEvent item : event.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.productId()));

            product.setStock(product.getStock() + item.quantity());
            productRepository.save(product);
            log.info("Restored {} units of product {} (stock now: {})",
                    item.quantity(), product.getName(), product.getStock());
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getSellerId(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}