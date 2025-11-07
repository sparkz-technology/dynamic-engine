package com.io.sdk.dynamic_engine.auth.service;

import com.io.sdk.dynamic_engine.auth.entity.Role;
import com.io.sdk.dynamic_engine.auth.entity.User;
import com.io.sdk.dynamic_engine.auth.repository.RoleRepository;
import com.io.sdk.dynamic_engine.auth.repository.UserRepository;
import com.io.sdk.dynamic_engine.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String email, String password, Set<String> roleNames) {

        if (userRepository.existsByEmail(email)) {
            throw new AppError(400, "Email already exists");
        }

        Set<Role> roles = roleRepository.findAll()
                .stream()
                .filter(r -> roleNames.contains(r.getName()))
                .collect(java.util.stream.Collectors.toSet());

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .active(true)
                .build();

        return userRepository.save(user);
    }
}
