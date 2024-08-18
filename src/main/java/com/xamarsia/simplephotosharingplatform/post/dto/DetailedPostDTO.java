package com.xamarsia.simplephotosharingplatform.post.dto;

import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;

public record DetailedPostDTO(
        @NotBlank(message = "PostDTO is required.") PostDTO postDTO,

        @NotBlank(message = "UserDTO is required.") UserDTO authorDTO) {
}
