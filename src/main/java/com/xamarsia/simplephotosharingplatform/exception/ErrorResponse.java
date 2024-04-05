package com.xamarsia.simplephotosharingplatform.exception;

import jakarta.validation.constraints.NotBlank;


public record ErrorResponse(
        @NotBlank(message = "Code is required.")
        int code,
        @NotBlank(message = "Message is required.")
        String message
) {
}
