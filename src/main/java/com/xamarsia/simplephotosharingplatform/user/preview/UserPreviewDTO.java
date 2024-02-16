package com.xamarsia.simplephotosharingplatform.user.preview;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record UserPreviewDTO (
        @NotEmpty
        @NotNull
        Long id,

        @NotEmpty
        @NotNull
        String fullName,

        @NotEmpty
        @NotNull
        String username) {
}
