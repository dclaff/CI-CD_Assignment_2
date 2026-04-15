package com.example.microservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for order responses.
 * Excludes the internal customerId foreign key — the relationship
 * is already expressed through the URL hierarchy
 * (e.g. /api/customers/{id}/orders).
 */
public class OrderDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate orderDate;
    private String status;

    public OrderDTO() {}

    public OrderDTO(Long id, String description, BigDecimal amount,
                    LocalDate orderDate, String status) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
