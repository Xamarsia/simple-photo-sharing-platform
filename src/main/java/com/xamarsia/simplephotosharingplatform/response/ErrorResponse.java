package com.xamarsia.simplephotosharingplatform.response;

import jakarta.validation.constraints.NotBlank;


public record ErrorResponse(
        @NotBlank(message = "Code is required.")
        int code,
        @NotBlank(message = "Message is required.")
        String message
) {
}
