package com.xamarsia.simplephotosharingplatform.post.dto;

import jakarta.validation.constraints.NotBlank;

public record PostDTO(
        @NotBlank(message = "Id is required.")
        Long id,

        @NotBlank(message = "Created date is required.")
        String createdDate,
        
        String updateDateTime,

        String description,

        @NotBlank(message = "Username is required.")
        String username) {
}
