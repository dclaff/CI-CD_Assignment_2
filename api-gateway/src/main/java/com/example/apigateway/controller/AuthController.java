package com.example.apigateway.controller;

import com.example.apigateway.dto.AuthRequest;
import com.example.apigateway.dto.AuthResponse;
import com.example.apigateway.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Hardcoded users for demo purposes
    private static final Map<String, String> USERS = Map.of(
            "admin", "password",
            "user", "userpass"
    );

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        String storedPassword = USERS.get(request.getUsername());
        if (storedPassword != null && storedPassword.equals(request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername());
            return Mono.just(ResponseEntity.ok(new AuthResponse(token)));
        }
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
