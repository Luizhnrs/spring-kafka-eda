package com.lab.order_service.service;

import com.lab.order_service.dto.request.CreateOrderRequest;
import com.lab.order_service.dto.request.OrderItemRequest;
import com.lab.order_service.dto.response.OrderItemResponse;
import com.lab.order_service.dto.response.OrderResponse;
import com.lab.order_service.entity.Order;
import com.lab.order_service.entity.OrderItem;
import com.lab.order_service.entity.OrderStatus;
import com.lab.order_service.event.OrderCreatedEvent;
import com.lab.order_service.event.OrderItemEvent;
import com.lab.order_service.event.OrderEventPublisher;
import com.lab.order_service.exception.ResourceNotFoundException;
import com.lab.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResponse create(CreateOrderRequest request, Long userId) {
        log.info("Creating order for userId: {}", userId);

        List<OrderItem> items = request.items().stream()
                .map(item -> buildOrderItem(item))
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        // Link items to order
        items.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}, status: {}", savedOrder.getId(), savedOrder.getStatus());

        // Publish Kafka event
        List<OrderItemEvent> itemEvents = savedOrder.getItems().stream()
                .map(item -> new OrderItemEvent(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                itemEvents);
        eventPublisher.publishOrderCreated(event);

        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user: " + userId);
        }

        return toResponse(order);
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, status);
    }

    private OrderItem buildOrderItem(OrderItemRequest item) {
        // Sem acesso direto ao banco do inventory-service.
        // Por enquanto armazenamos productId e quantity.
        // O productName e unitPrice serão preenchidos futuramente via eventos.
        return OrderItem.builder()
                .productId(item.productId())
                .productName("Product-" + item.productId())
                .unitPrice(BigDecimal.ZERO)
                .quantity(item.quantity())
                .subtotal(BigDecimal.ZERO)
                .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses);
    }
}