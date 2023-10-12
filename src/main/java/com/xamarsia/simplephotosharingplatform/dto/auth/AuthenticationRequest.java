package com.xamarsia.simplephotosharingplatform.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotEmpty
        @NotNull(message = "Email is required.")
        String email,

        @NotEmpty
        @NotNull(message = "Email is required.")
        String password) {
}