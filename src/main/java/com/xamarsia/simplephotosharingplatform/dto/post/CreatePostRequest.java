package com.xamarsia.simplephotosharingplatform.dto.post;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreatePostRequest {
    private String description;

    @NotNull(message = "Image is required.")
    private MultipartFile image;
}