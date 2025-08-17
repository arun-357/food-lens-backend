package com.foodanalyzer.backend.controller;

import com.foodanalyzer.backend.model.ResponseWrapper;
import com.foodanalyzer.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper> register(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseWrapper(false, "Email and password are required"));
            }
            // Email validation
            String emailRegex = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$";
            if (!email.matches(emailRegex)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseWrapper(false, "Invalid email format or domain"));
            }
            // Check password length
            if (password.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(new ResponseWrapper(false, "Password must be at least 8 characters long"));
            }
            // Password validation
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
            if (!password.matches(passwordRegex)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseWrapper(false, "Password must contain at least one uppercase letter, one lowercase letter, and one number"));
            }
            String token = authService.register(email, password);
            return ResponseEntity.ok(new ResponseWrapper(true, "Registered"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseWrapper(false, "Email and password are required"));
            }
            String token = authService.login(email, password);
            return ResponseEntity.ok(new ResponseWrapper(true, "Login successful", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper(false, e.getMessage()));
        }
    }
}