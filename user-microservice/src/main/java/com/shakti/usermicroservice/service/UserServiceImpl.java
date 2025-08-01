package com.shakti.usermicroservice.service;

import com.shakti.usermicroservice.dto.UserRegistrationRequest;
import com.shakti.usermicroservice.dto.UserResponse;
import com.shakti.usermicroservice.entity.Role;
import com.shakti.usermicroservice.entity.User;
import com.shakti.usermicroservice.entity.UserStatus;
import com.shakti.usermicroservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already registered - {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .name(request.getName())
                .mobileNo(request.getMobileNo())
                .dob(request.getDob())
                .emailVerified(false) // default false; implement verification flow later
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully with id: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user profile with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        return mapToUserResponse(user);
    }

    
    @Override
    public boolean validateCredentials(String email, String password) {
        log.debug("Validating credentials for email: {}", email);

        return userRepository.findByEmail(email)
                .map(user -> BCrypt.checkpw(password, user.getPassword()))
                .orElseGet(() -> {
                    log.warn("No user found for email during credential validation: {}", email);
                    return false;
                });
    }

    // Internal mapper method to convert User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .mobileNo(user.getMobileNo())
                .dob(user.getDob())
                .profilePictureUrl(user.getProfilePictureUrl())
                .emailVerified(user.isEmailVerified())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole())
                .build();
    }
}
