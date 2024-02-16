package com.xamarsia.simplephotosharingplatform.dto.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class CreatePostRequest {
    private String description;

    @NotEmpty
    private MultipartFile image;
}