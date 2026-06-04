package com.example.inventory_service.controller;

import com.example.inventory_service.dto.request.ProductRequest;
import com.example.inventory_service.dto.response.ProductResponse;
import com.example.inventory_service.exception.AccessDeniedException;
import com.example.inventory_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("BUYER");

        if (!"SELLER".equals(role)) {
            throw new AccessDeniedException(role, "create products");
        }

        // sellerId vem do claim userId do JWT emitido pelo user-service
        Long sellerId = (Long) authentication.getCredentials();
        if (sellerId == null) {
            throw new IllegalArgumentException("Invalid user token: missing userId");
        }
        ProductResponse response = productService.create(request, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}