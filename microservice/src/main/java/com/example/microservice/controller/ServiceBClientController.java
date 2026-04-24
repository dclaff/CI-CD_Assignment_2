package com.example.microservice.controller;

import com.example.microservice.client.ServiceBClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-a")
public class ServiceBClientController {

    private final ServiceBClient serviceBClient;

    public ServiceBClientController(ServiceBClient serviceBClient) {
        this.serviceBClient = serviceBClient;
    }

    @GetMapping("/call-service-b")
    public String callServiceB() {
        String response = serviceBClient.getGreeting();
        return "Response from Service B: " + response;
    }
}