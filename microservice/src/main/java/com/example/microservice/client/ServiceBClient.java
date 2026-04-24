package com.example.microservice.client;

import com.example.microservice.dto.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceBClient {

    private static final Logger log = LoggerFactory.getLogger(ServiceBClient.class);

    private final RestTemplate restTemplate;

    public ServiceBClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "service-b", fallbackMethod = "getProductFallback")
    @Retry(name = "service-b")
    public ProductDTO getProduct(Long productId) {
        log.info("Calling Service B to look up product id={}", productId);
        return restTemplate.getForObject(
                "http://service-b/api/products/{id}", ProductDTO.class, productId);
    }

    public ProductDTO getProductFallback(Long productId, Exception e) {
        log.warn("Service B unavailable when looking up product id={}. Error: {}", productId, e.getMessage());
        return null; // null signals caller to proceed without product enrichment
    }

    @CircuitBreaker(name = "service-b", fallbackMethod = "greetFallback")
    @Retry(name = "service-b")
    public String getGreeting() {
        log.info("Calling Service B for greeting");
        return restTemplate.getForObject("http://service-b/api/service-b/greet", String.class);
    }

    public String greetFallback(Exception e) {
        log.warn("Service B is unavailable, returning fallback response. Error: {}", e.getMessage());
        return "Service B is currently unavailable. Please try again later.";
    }
}
