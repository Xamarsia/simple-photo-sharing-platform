package com.xamarsia.simplephotosharingplatform.post.dto;

import jakarta.validation.constraints.NotBlank;

public record PostPreviewDTO(
        @NotBlank(message = "Id is required.")
        Long id
) {
}
