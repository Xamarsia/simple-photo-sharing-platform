package com.xamarsia.simplephotosharingplatform.dto.post;

import com.xamarsia.simplephotosharingplatform.dto.user.UserDTO;
import com.xamarsia.simplephotosharingplatform.enums.LikeState;

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
