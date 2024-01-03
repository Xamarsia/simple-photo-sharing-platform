package com.xamarsia.simplephotosharingplatform.post.preview;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostPreviewDTO(
        @NotEmpty
        @NotNull
        Long id,

        @NotEmpty
        @NotNull
        String imageUrl
        ) {
}
