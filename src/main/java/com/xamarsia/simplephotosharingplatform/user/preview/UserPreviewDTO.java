package com.xamarsia.simplephotosharingplatform.user.preview;

import com.xamarsia.simplephotosharingplatform.user.State;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;


public record UserPreviewDTO (
        @NotBlank(message = "Id is required.")
        Long id,

        @NotBlank(message = "Full name is required.")
        String fullName,

        @NotBlank(message = "Username is required.")
        String username,

        @NotBlank(message = "State is required.")
        @Enumerated(EnumType.STRING)
        State state) {
}
