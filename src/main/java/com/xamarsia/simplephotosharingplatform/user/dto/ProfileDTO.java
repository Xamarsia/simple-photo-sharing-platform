package com.xamarsia.simplephotosharingplatform.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileDTO(
        @NotBlank(message = "Followings count is required.") 
        Integer followingsCount,

        @NotBlank(message = "Followers count is required.") 
        Integer followersCount,

        @NotBlank(message = "Posts count is requiraued.")  // requiraued =)
        Integer postsCount,

        @NotBlank(message = "User preview DTO is required.") 
        UserDTO userDTO) {
}
