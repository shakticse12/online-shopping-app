package com.shakti.usermicroservice.dto;

import com.shakti.usermicroservice.entity.Role;
import com.shakti.usermicroservice.entity.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String mobileNo;
    private LocalDate dob;
    private String profilePictureUrl;
    private boolean emailVerified;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;
}
