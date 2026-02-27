package com.ecoride.auth.controller;

import com.ecoride.auth.dto.LoginRequest;
import com.ecoride.auth.dto.RegisterRequest;
import com.ecoride.auth.dto.TokenResponse;
import com.ecoride.auth.service.AuthService;
import com.ecoride.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.ok("Registered successfully", authService.register(req));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok("Login successful", authService.login(req));
    }
}
