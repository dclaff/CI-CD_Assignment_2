package com.example.microservice.service;

import com.example.microservice.client.ServiceBClient;
import com.example.microservice.dto.*;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.model.Order;
import com.example.microservice.repository.CustomerRepository;
import com.example.microservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Business-logic layer for Order operations.
 * Ensures the parent Customer exists before any order manipulation
 * and converts between entities and DTOs.
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ServiceBClient serviceBClient;

    public OrderService(OrderRepository orderRepo, CustomerRepository customerRepo,
                        ServiceBClient serviceBClient) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.serviceBClient = serviceBClient;
    }

    /* ---- orders for a customer (paginated) ---- */
    public PagedResponse<OrderDTO> getOrdersByCustomer(Long customerId, int page, int size) {
        validateCustomerExists(customerId);
        int offset = page * size;
        List<Order> orders = orderRepo.findByCustomerId(customerId, offset, size);
        long total = orderRepo.countByCustomerId(customerId);

        List<OrderDTO> dtos = orders.stream().map(this::toDTO).toList();
        return new PagedResponse<>(dtos, page, size, total);
    }

    /* ---- single order ---- */
    public OrderDTO getOrder(Long customerId, Long orderId) {
        validateCustomerExists(customerId);
        Order order = orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId + " for customer: " + customerId));
        return toDTO(order);
    }

    /* ---- create order under customer ---- */
    public OrderDTO createOrder(Long customerId, CreateOrderRequest request) {
        validateCustomerExists(customerId);

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setDescription(request.getDescription());
        order.setAmount(request.getAmount());
        order.setOrderDate(request.getOrderDate());
        order.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");

        // If a productId is provided, validate it exists in Service B and snapshot the product name
        if (request.getProductId() != null) {
            log.info("Order creation: looking up product id={} via Service B", request.getProductId());
            ProductDTO product = serviceBClient.getProduct(request.getProductId());
            if (product != null) {
                order.setProductId(product.getId());
                order.setProductName(product.getName());
                log.info("Product '{}' confirmed via Service B for order", product.getName());
            } else {
                log.warn("Service B unavailable — proceeding without product enrichment");
                order.setProductId(request.getProductId());
            }
        }

        Order saved = orderRepo.save(order);
        return toDTO(saved);
    }

    /* ---- update ---- */
    public OrderDTO updateOrder(Long customerId, Long orderId, CreateOrderRequest request) {
        validateCustomerExists(customerId);
        Order order = orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId + " for customer: " + customerId));

        order.setDescription(request.getDescription());
        order.setAmount(request.getAmount());
        order.setOrderDate(request.getOrderDate());
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        Order updated = orderRepo.save(order);
        return toDTO(updated);
    }

    /* ---- delete ---- */
    public void deleteOrder(Long customerId, Long orderId) {
        validateCustomerExists(customerId);
        if (!orderRepo.deleteByIdAndCustomerId(orderId, customerId)) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + orderId + " for customer: " + customerId);
        }
    }

    /* ---- all orders across customers, with date filtering & sorting (paginated) ---- */
    public PagedResponse<OrderDTO> getAllOrders(int page, int size,
                                                LocalDate startDate, LocalDate endDate,
                                                String sortDir) {
        int offset = page * size;
        List<Order> orders = orderRepo.findAll(offset, size, startDate, endDate, sortDir);
        long total = orderRepo.countAll(startDate, endDate);

        List<OrderDTO> dtos = orders.stream().map(this::toDTO).toList();
        return new PagedResponse<>(dtos, page, size, total);
    }

    /* ---- private helpers ---- */
    private void validateCustomerExists(Long customerId) {
        customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setAmount(order.getAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setProductId(order.getProductId());
        dto.setProductName(order.getProductName());
        return dto;
    }
}
