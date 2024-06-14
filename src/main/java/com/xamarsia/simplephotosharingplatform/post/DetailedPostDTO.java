package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;

import jakarta.validation.constraints.NotBlank;

public record DetailedPostDTO(
        @NotBlank(message = "PostDTO is required.") PostDTO postDTO,

        @NotBlank(message = "UserPreviewDTO is required.") UserPreviewDTO authorDTO) {
}
