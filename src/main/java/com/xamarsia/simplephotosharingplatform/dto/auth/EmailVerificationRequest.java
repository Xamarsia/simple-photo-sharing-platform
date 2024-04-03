package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
        @NotBlank(message = "Email is required.")
        @UniqueEmail(message = "A user with email '${validatedValue}' already exists.")
        String email) {
}