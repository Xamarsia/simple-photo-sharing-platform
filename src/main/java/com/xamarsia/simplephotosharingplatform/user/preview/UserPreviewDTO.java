package com.xamarsia.simplephotosharingplatform.user.preview;

import com.xamarsia.simplephotosharingplatform.user.State;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        String username,

        @NotEmpty
        @NotNull
        @Enumerated(EnumType.STRING)
        State state) {
}
