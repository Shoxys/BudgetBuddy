package com.shoxys.budgetbuddy_backend.Controller;

import com.shoxys.budgetbuddy_backend.DTO.LoginRequest;
import com.shoxys.budgetbuddy_backend.DTO.RegisterRequest;
import com.shoxys.budgetbuddy_backend.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
