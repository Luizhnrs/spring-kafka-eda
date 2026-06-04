package com.example.inventory_service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    int status,
    String message,
    List<String> errors,
    LocalDateTime timestamp,
    String path
) {}