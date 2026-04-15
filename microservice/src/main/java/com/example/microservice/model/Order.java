package com.example.microservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {
    private Long id;
    private Long customerId;
    private String description;
    private BigDecimal amount;
    private LocalDate orderDate;
    private String status;

    public Order() {}

    public Order(Long id, Long customerId, String description, BigDecimal amount,
                 LocalDate orderDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.description = description;
        this.amount = amount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
