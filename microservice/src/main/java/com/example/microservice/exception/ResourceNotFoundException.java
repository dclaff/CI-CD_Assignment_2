package com.example.microservice.exception;

/**
 * Thrown when a requested resource (Customer, Order, etc.) does not exist.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
