package com.xamarsia.simplephotosharingplatform.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdatePostRequest {
    private String description;
    private MultipartFile image;

    public boolean isEmpty() {
        return description == null && image == null;
    }
}