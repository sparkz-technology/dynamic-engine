package com.io.sdk.dynamic_engine.auth.controller;

import com.io.sdk.dynamic_engine.auth.dto.LoginRequest;
import com.io.sdk.dynamic_engine.auth.dto.LoginResponse;
import com.io.sdk.dynamic_engine.auth.dto.RefreshRequest;
import com.io.sdk.dynamic_engine.auth.dto.RefreshResponse;
import com.io.sdk.dynamic_engine.auth.service.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@SecurityRequirements()
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody(required = true) @Valid LoginRequest request) {
        LoginResponse tokens = authService.login(request);

        return ResponseEntity.ok(Map.of(
                "accessToken", tokens.getAccessToken(),
                "refreshToken", tokens.getRefreshToken(),
                "tokenType", "Bearer"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody(required = true) RefreshRequest request) {
        RefreshResponse tokens = authService.refresh(request);

        return ResponseEntity.ok(Map.of(
                "accessToken", tokens.getAccessToken(),
                "refreshToken", tokens.getRefreshToken(),
                "tokenType", "Bearer"));
    }

}
