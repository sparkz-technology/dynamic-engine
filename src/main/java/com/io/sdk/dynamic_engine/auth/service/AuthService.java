package com.io.sdk.dynamic_engine.auth.service;

import com.io.sdk.dynamic_engine.auth.dto.LoginRequest;
import com.io.sdk.dynamic_engine.auth.dto.LoginResponse;
import com.io.sdk.dynamic_engine.auth.dto.RefreshRequest;
import com.io.sdk.dynamic_engine.auth.dto.RefreshResponse;
import com.io.sdk.dynamic_engine.auth.entity.User;
import com.io.sdk.dynamic_engine.auth.repository.UserRepository;
import com.io.sdk.dynamic_engine.auth.security.JwtTokenProvider;
import com.io.sdk.dynamic_engine.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppError(404, "User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppError(400, "Invalid credentials");
        }
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return new LoginResponse(accessToken, refreshToken);
    }

    public RefreshResponse refresh(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new AppError(400, "Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppError(400, "User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(user.getEmail(),
                user.getRoles());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }

}
