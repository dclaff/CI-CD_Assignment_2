package com.example.microservice.controller;

import com.example.microservice.dto.*;
import com.example.microservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/customers/{customerId}/orders?page=0&size=10
     * Returns paginated orders for a specific customer.
     */
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<PagedResponse<OrderDTO>> listByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, page, size));
    }

    /**
     * GET /api/customers/{customerId}/orders/{orderId}
     * Returns a single order for the given customer.
     */
    @GetMapping("/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> get(
            @PathVariable Long customerId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(customerId, orderId));
    }

    /**
     * POST /api/customers/{customerId}/orders
     * Creates an order linked to the specified customer.
     */
    @PostMapping("/customers/{customerId}/orders")
    public ResponseEntity<OrderDTO> create(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO created = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/customers/{customerId}/orders/{orderId}
     * Updates an existing order. Returns 404 if either the customer or order is not found.
     */
    @PutMapping("/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> update(
            @PathVariable Long customerId,
            @PathVariable Long orderId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(customerId, orderId, request));
    }

    /**
     * DELETE /api/customers/{customerId}/orders/{orderId}
     * Deletes an order. Returns 404 if not found.
     */
    @DeleteMapping("/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long customerId,
            @PathVariable Long orderId) {
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/orders?page=0&size=10&startDate=2025-01-01&endDate=2025-12-31&sort=ASC
     * Returns all orders across customers with optional date-range filtering
     * and date-based sorting. All parameters are optional; dates use YYYY-MM-DD format.
     */
    @GetMapping("/orders")
    public ResponseEntity<PagedResponse<OrderDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DESC") String sort) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size, startDate, endDate, sort));
    }
}
