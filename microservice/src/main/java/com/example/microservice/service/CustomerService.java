package com.example.microservice.service;

import com.example.microservice.dto.*;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.model.Customer;
import com.example.microservice.model.Order;
import com.example.microservice.repository.CustomerRepository;
import com.example.microservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Business-logic layer for Customer operations.
 * Maps between domain entities and DTOs and delegates persistence
 * to the repository layer.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    public CustomerService(CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    /* ---- list (paginated) ---- */
    public PagedResponse<CustomerDTO> getAllCustomers(int page, int size) {
        int offset = page * size;
        List<Customer> customers = customerRepo.findAll(offset, size);
        long total = customerRepo.count();

        List<CustomerDTO> dtos = customers.stream().map(c -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setTotalOrders(customerRepo.countOrdersByCustomerId(c.getId()));
            return dto;
        }).toList();

        return new PagedResponse<>(dtos, page, size, total);
    }

    /* ---- single customer with nested orders ---- */
    public CustomerDetailDTO getCustomerById(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        List<Order> orders = orderRepo.findByCustomerId(id, 0, Integer.MAX_VALUE);

        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setOrders(orders.stream().map(this::toOrderDTO).toList());
        return dto;
    }

    /* ---- create ---- */
    public CustomerDetailDTO createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setCreatedAt(LocalDate.now());

        Customer saved = customerRepo.save(customer);

        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setEmail(saved.getEmail());
        dto.setPhone(saved.getPhone());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setOrders(List.of());
        return dto;
    }

    /* ---- update ---- */
    public CustomerDetailDTO updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updated = customerRepo.save(customer);
        List<Order> orders = orderRepo.findByCustomerId(id, 0, Integer.MAX_VALUE);

        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(updated.getId());
        dto.setName(updated.getName());
        dto.setEmail(updated.getEmail());
        dto.setPhone(updated.getPhone());
        dto.setCreatedAt(updated.getCreatedAt());
        dto.setOrders(orders.stream().map(this::toOrderDTO).toList());
        return dto;
    }

    /* ---- delete (cascading delete handled by DB foreign-key constraint) ---- */
    public void deleteCustomer(Long id) {
        if (!customerRepo.deleteById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
    }

    /* ---- mapping helper ---- */
    private OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setAmount(order.getAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        return dto;
    }
}
