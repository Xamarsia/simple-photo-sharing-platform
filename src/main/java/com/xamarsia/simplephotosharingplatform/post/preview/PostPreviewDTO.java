package com.xamarsia.simplephotosharingplatform.post.preview;

import jakarta.validation.constraints.NotBlank;

public record PostPreviewDTO(
        @NotBlank(message = "Id is required.")
        Long id
) {
}
