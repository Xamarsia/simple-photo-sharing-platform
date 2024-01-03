package com.xamarsia.simplephotosharingplatform.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record CreatePostRequest(
        @NotNull @NotEmpty String imageUrl,
        String description
        ) {
}
