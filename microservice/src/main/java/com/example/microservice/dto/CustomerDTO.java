package com.example.microservice.dto;

/**
 * Lightweight DTO for customer list responses.
 * Intentionally excludes phone and internal timestamps — exposes only
 * the fields an API consumer typically needs when browsing customers.
 */
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private int totalOrders;

    public CustomerDTO() {}

    public CustomerDTO(Long id, String name, String email, int totalOrders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalOrders = totalOrders;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
}
