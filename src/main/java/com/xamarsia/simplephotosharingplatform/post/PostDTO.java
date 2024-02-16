package com.xamarsia.simplephotosharingplatform.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostDTO(
        @NotEmpty
        @NotNull
        Long id,

        @NotEmpty
        @NotNull
        String createdDate,

        String description,

        @NotEmpty
        @NotNull
        String username) {
}
