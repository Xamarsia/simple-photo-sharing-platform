package com.xamarsia.simplephotosharingplatform.requests.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Username.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RegisterRequest {
    @NotBlank(message = "Username is required.")
    @UniqueUsername(message = "A user with username '${validatedValue}' already exists.")
    private String username;

    private String fullName;

    private MultipartFile image;
}
