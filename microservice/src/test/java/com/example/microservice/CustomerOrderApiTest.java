package com.example.microservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerOrderApiTest {

    @Autowired
    private MockMvc mvc;

    /* ==========================
     *  Customer endpoints
     * ========================== */

    @Test
    public void listCustomers_returnsPaginatedResult() throws Exception {
        mvc.perform(get("/api/customers?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.content[0].totalOrders").isNumber());
    }

    @Test
    public void getCustomer_returnsDetailWithOrders() throws Exception {
        mvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.createdAt").value("2025-01-15"))
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders", hasSize(greaterThan(0))));
    }

    @Test
    public void getCustomer_notFound_returns404() throws Exception {
        mvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    public void createCustomer_validInput_returns201() throws Exception {
        String json = """
                { "name": "Dan Test", "email": "dan@test.com", "phone": "555-9999" }
                """;
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dan Test"))
                .andExpect(jsonPath("$.email").value("dan@test.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.orders", hasSize(0)));
    }

    @Test
    public void createCustomer_missingName_returns400() throws Exception {
        String json = """
                { "email": "bad@test.com" }
                """;
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    public void createCustomer_invalidEmail_returns400() throws Exception {
        String json = """
                { "name": "Test", "email": "not-an-email" }
                """;
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("email")));
    }

    @Test
    public void updateCustomer_validInput_returns200() throws Exception {
        String json = """
                { "name": "Bob Updated", "email": "bob.updated@example.com" }
                """;
        mvc.perform(put("/api/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob Updated"));
    }

    @Test
    public void updateCustomer_notFound_returns404() throws Exception {
        String json = """
                { "name": "Nobody", "email": "no@one.com" }
                """;
        mvc.perform(put("/api/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    /* ==========================
     *  Order endpoints (nested)
     * ========================== */

    @Test
    public void listOrdersByCustomer_returnsPaginated() throws Exception {
        mvc.perform(get("/api/customers/1/orders?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    public void createOrder_validInput_returns201() throws Exception {
        String json = """
                {
                    "description": "Test Widget",
                    "amount": 49.99,
                    "orderDate": "2025-11-01",
                    "status": "PENDING"
                }
                """;
        mvc.perform(post("/api/customers/2/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Widget"))
                .andExpect(jsonPath("$.amount").value(49.99))
                .andExpect(jsonPath("$.orderDate").value("2025-11-01"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void createOrder_invalidAmount_returns400() throws Exception {
        String json = """
                {
                    "description": "Bad Order",
                    "amount": -10,
                    "orderDate": "2025-11-01"
                }
                """;
        mvc.perform(post("/api/customers/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("Amount must be positive"))));
    }

    @Test
    public void createOrder_missingDate_returns400() throws Exception {
        String json = """
                { "description": "No Date", "amount": 10.00 }
                """;
        mvc.perform(post("/api/customers/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("Order date is required"))));
    }

    @Test
    public void createOrder_customerNotFound_returns404() throws Exception {
        String json = """
                { "description": "Orphan", "amount": 10.00, "orderDate": "2025-11-01" }
                """;
        mvc.perform(post("/api/customers/999/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }

    @Test
    public void getOrder_single_returnsOrder() throws Exception {
        mvc.perform(get("/api/customers/1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderDate").exists());
    }

    @Test
    public void getOrder_notFound_returns404() throws Exception {
        mvc.perform(get("/api/customers/1/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrder_returns204() throws Exception {
        // Create an order first, then delete it
        String json = """
                { "description": "To Delete", "amount": 5.00, "orderDate": "2025-12-25" }
                """;
        String response = mvc.perform(post("/api/customers/3/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract the id from the response
        String orderId = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(delete("/api/customers/3/orders/" + orderId))
                .andExpect(status().isNoContent());
    }

    /* ==========================
     *  All-orders endpoint (date filtering & sorting)
     * ========================== */

    @Test
    public void listAllOrders_paginated() throws Exception {
        mvc.perform(get("/api/orders?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    public void listAllOrders_filterByDateRange() throws Exception {
        mvc.perform(get("/api/orders?startDate=2025-04-01&endDate=2025-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    public void listAllOrders_sortAscending() throws Exception {
        mvc.perform(get("/api/orders?sort=ASC&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderDate").exists());
    }

    @Test
    public void listAllOrders_invalidDateFormat_returns400() throws Exception {
        mvc.perform(get("/api/orders?startDate=not-a-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("YYYY-MM-DD")));
    }

    /* ==========================
     *  Cascading delete
     * ========================== */

    @Test
    public void deleteCustomer_cascadesOrderDeletion() throws Exception {
        // Create a new customer
        String custJson = """
                { "name": "Temp User", "email": "temp@test.com" }
                """;
        String custResponse = mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(custJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String custId = custResponse.replaceAll(".*\"id\":(\\d+).*", "$1");

        // Create an order for that customer
        String orderJson = """
                { "description": "Cascade Test", "amount": 10.00, "orderDate": "2025-12-31" }
                """;
        mvc.perform(post("/api/customers/" + custId + "/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated());

        // Delete the customer — orders should cascade
        mvc.perform(delete("/api/customers/" + custId))
                .andExpect(status().isNoContent());

        // Customer should be gone
        mvc.perform(get("/api/customers/" + custId))
                .andExpect(status().isNotFound());
    }
}
