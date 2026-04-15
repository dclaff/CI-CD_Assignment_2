package com.example.microservice.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Detailed DTO returned when fetching a single customer.
 * Includes nested order list so the client can view the full
 * customer profile in one request.
 */
public class CustomerDetailDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate createdAt;
    private List<OrderDTO> orders;

    public CustomerDetailDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public List<OrderDTO> getOrders() { return orders; }
    public void setOrders(List<OrderDTO> orders) { this.orders = orders; }
}
