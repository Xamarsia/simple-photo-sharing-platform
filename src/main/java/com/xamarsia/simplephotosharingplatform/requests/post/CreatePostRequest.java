package com.xamarsia.simplephotosharingplatform.requests.post;


import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(
    String description,

    @NotNull(message = "Image is required.") 
    MultipartFile image
    ) {
}
