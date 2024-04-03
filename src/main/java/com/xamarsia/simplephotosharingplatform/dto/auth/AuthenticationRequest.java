package com.xamarsia.simplephotosharingplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Password is required.")
        String password) {
}