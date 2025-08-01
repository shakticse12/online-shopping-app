package com.shakti.usermicroservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 15)
    private String mobileNo;

    private java.time.LocalDate dob;
}
