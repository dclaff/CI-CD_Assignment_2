package com.example.microservice.controller;

import com.example.microservice.dto.*;
import com.example.microservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /api/customers?page=0&size=10
     * Returns a paginated list of customers (lightweight DTO without orders).
     */
    @GetMapping
    public ResponseEntity<PagedResponse<CustomerDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    /**
     * GET /api/customers/{id}
     * Returns full customer details including nested orders.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    /**
     * POST /api/customers
     * Creates a new customer. Validates the request body.
     */
    @PostMapping
    public ResponseEntity<CustomerDetailDTO> create(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerDetailDTO created = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/customers/{id}
     * Updates an existing customer. Returns 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDetailDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    /**
     * DELETE /api/customers/{id}
     * Deletes a customer and all associated orders (cascade).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
