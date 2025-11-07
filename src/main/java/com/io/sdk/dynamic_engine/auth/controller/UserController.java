package com.io.sdk.dynamic_engine.auth.controller;

import com.io.sdk.dynamic_engine.auth.entity.User;
import com.io.sdk.dynamic_engine.auth.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public User createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Set<String> roles) {
        return userService.createUser(email, password, roles);
    }
}
