package com.xamarsia.simplephotosharingplatform.dto.post;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreatePostRequest {
    private String description;

    @NotNull(message = "Image is required.")
    private MultipartFile image;
}