package com.xamarsia.simplephotosharingplatform.requests.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import com.xamarsia.simplephotosharingplatform.common.validator.Username.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RegisterRequest {
    @NotBlank(message = "Email is required.")
    @UniqueEmail(message = "A user with email '${validatedValue}' already exists.")
    private String email;

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Username is required.")
    @UniqueUsername(message = "A user with username '${validatedValue}' already exists.")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least {min} characters.")
    private String password;

    private MultipartFile image;

   @NotNull(message = "Email verification code is required.")
   private Integer emailVerificationCode;
}
