package com.ecoride.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8)
    private String password;

    private String department;

    @Min(1) @Max(6)
    private Integer year;
}
