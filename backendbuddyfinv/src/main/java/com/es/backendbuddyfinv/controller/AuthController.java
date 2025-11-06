package com.es.backendbuddyfinv.controller;

import com.es.backendbuddyfinv.dto.AuthRequest;
import com.es.backendbuddyfinv.dto.AuthResponse;

import com.es.backendbuddyfinv.service.impl.AuthService;


import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}