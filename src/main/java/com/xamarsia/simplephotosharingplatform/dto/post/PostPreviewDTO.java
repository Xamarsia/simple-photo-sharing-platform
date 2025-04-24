package com.xamarsia.simplephotosharingplatform.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostPreviewDTO(
        @NotBlank(message = "Id is required.")
        Long id
) {
}
