package com.xamarsia.simplephotosharingplatform.post.dto;

import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;

import jakarta.validation.constraints.NotBlank;

public record DetailedPostDTO(
        @NotBlank(message = "PostDTO is required.") PostDTO postDTO,

        @NotBlank(message = "UserPreviewDTO is required.") UserPreviewDTO authorDTO) {
}
