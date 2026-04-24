package com.example.serviceb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-b")
public class ServiceBController {

    @GetMapping("/greet")
    public String greet() {
        return "Hello from Service B!";
    }
}