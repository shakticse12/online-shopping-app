package com.shakti.usermicroservice.service;

import com.shakti.usermicroservice.dto.UserRegistrationRequest;
import com.shakti.usermicroservice.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserByEmail(String email);
    boolean validateCredentials(String email, String password);
}
