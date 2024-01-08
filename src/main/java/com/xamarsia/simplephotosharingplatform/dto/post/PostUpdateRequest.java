package com.xamarsia.simplephotosharingplatform.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostUpdateRequest(
        @NotNull @NotEmpty String imageUrl,
        String description
) {
}