package com.xamarsia.simplephotosharingplatform.post.dto;

import com.xamarsia.simplephotosharingplatform.post.LikeState;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record DetailedPostDTO(
        @NotBlank(message = "PostDTO is required.") 
        PostDTO postDTO,

        @NotBlank(message = "UserDTO is required.") 
        UserDTO authorDTO,

        @NotBlank(message = "State is required.")
        @Enumerated(EnumType.STRING)
        LikeState state
        ) {
}
