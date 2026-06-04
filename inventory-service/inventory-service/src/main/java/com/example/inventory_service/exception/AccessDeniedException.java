package com.example.inventory_service.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String role, String action) {
        super(String.format("Access denied: role '%s' is not allowed to %s", role, action));
    }
}