package com.example.microservice.service;

import com.example.microservice.client.ServiceBClient;
import com.example.microservice.dto.CreateOrderRequest;
import com.example.microservice.dto.OrderDTO;
import com.example.microservice.dto.ProductDTO;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.model.Customer;
import com.example.microservice.model.Order;
import com.example.microservice.repository.CustomerRepository;
import com.example.microservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private ServiceBClient serviceBClient;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "Alice", "alice@example.com", "555-0101", LocalDate.of(2025, 1, 1));
    }

    @Test
    void createOrder_withoutProductId_savesOrderWithoutCallingServiceB() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        Order saved = new Order(10L, 1L, "Test item", BigDecimal.valueOf(50), LocalDate.now(), "PENDING");
        when(orderRepo.save(any())).thenReturn(saved);

        CreateOrderRequest req = new CreateOrderRequest();
        req.setDescription("Test item");
        req.setAmount(BigDecimal.valueOf(50));
        req.setOrderDate(LocalDate.now());

        OrderDTO dto = orderService.createOrder(1L, req);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Test item");
        verifyNoInteractions(serviceBClient);
    }

    @Test
    void createOrder_withValidProductId_enrichesOrderWithProductName() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setName("Laptop");
        when(serviceBClient.getProduct(1L)).thenReturn(product);

        Order saved = new Order(11L, 1L, "Purchase", BigDecimal.valueOf(999), LocalDate.now(), "PENDING");
        saved.setProductId(1L);
        saved.setProductName("Laptop");
        when(orderRepo.save(any())).thenReturn(saved);

        CreateOrderRequest req = new CreateOrderRequest();
        req.setDescription("Purchase");
        req.setAmount(BigDecimal.valueOf(999));
        req.setOrderDate(LocalDate.now());
        req.setProductId(1L);

        OrderDTO dto = orderService.createOrder(1L, req);

        assertThat(dto.getProductId()).isEqualTo(1L);
        assertThat(dto.getProductName()).isEqualTo("Laptop");
        verify(serviceBClient).getProduct(1L);
    }

    @Test
    void createOrder_whenServiceBUnavailable_proceedsWithoutProductName() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceBClient.getProduct(99L)).thenReturn(null); // fallback returns null

        Order saved = new Order(12L, 1L, "Item", BigDecimal.valueOf(10), LocalDate.now(), "PENDING");
        saved.setProductId(99L);
        when(orderRepo.save(any())).thenReturn(saved);

        CreateOrderRequest req = new CreateOrderRequest();
        req.setDescription("Item");
        req.setAmount(BigDecimal.valueOf(10));
        req.setOrderDate(LocalDate.now());
        req.setProductId(99L);

        OrderDTO dto = orderService.createOrder(1L, req);

        assertThat(dto.getId()).isEqualTo(12L);
        assertThat(dto.getProductName()).isNull();
    }

    @Test
    void createOrder_customerNotFound_throwsResourceNotFoundException() {
        when(customerRepo.findById(999L)).thenReturn(Optional.empty());

        CreateOrderRequest req = new CreateOrderRequest();
        req.setDescription("item");
        req.setAmount(BigDecimal.TEN);
        req.setOrderDate(LocalDate.now());

        assertThatThrownBy(() -> orderService.createOrder(999L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getOrder_notFound_throwsResourceNotFoundException() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepo.findByIdAndCustomerId(eq(99L), eq(1L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
