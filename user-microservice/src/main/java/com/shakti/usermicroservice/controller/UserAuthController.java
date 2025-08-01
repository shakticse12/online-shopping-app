package com.shakti.usermicroservice.controller;

import com.shakti.usermicroservice.dto.*;
import com.shakti.usermicroservice.security.JwtUtil;
import com.shakti.usermicroservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserAuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        try {
            userService.registerUser(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            log.error("Registration failure for email: {}, reason: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
    

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        boolean valid = userService.validateCredentials(request.getEmail(), request.getPassword());

        if (!valid) {
            log.warn("Invalid login attempt for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponse user = userService.getUserByEmail(request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        log.info("Login successful for email: {}", request.getEmail());

        return ResponseEntity.ok(new UserAuthenticationResponse(token));
    }
    

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        UserResponse userResponse = userService.getUserByEmail(email);

        log.info("Profile fetched for email: {}", email);

        return ResponseEntity.ok(userResponse);
    }
}
