package com.xamarsia.simplephotosharingplatform.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostUpdateRequest {
    private String description;
    private MultipartFile image;
}