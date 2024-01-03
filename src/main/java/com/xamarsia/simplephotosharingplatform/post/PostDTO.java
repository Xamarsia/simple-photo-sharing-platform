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

        @NotEmpty
        @NotNull
        String imageUrl,

        String description,

        @NotEmpty
        @NotNull
        String username) {
}
