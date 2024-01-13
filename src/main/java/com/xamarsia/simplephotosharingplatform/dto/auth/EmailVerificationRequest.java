package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EmailVerificationRequest(
        @NotEmpty
        @NotNull(message = "Email is required.")
        @UniqueEmail
        String email) {
}