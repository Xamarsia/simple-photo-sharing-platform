package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import com.xamarsia.simplephotosharingplatform.common.validator.Username.UniqueUsername;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    @UniqueEmail
    @NotEmpty(message = "Email is required.")
    private String email;

    @NotNull
    @NotEmpty
    private String fullName;

    @NotNull
    @NotEmpty
    @UniqueUsername
    private String username;

    @NotEmpty
    @NotNull(message = "Password is required.")
    @Size(min = 6, message = "Password should be at least 6 characters.")
    private String password;

    @NotNull(message = "Email verification code is required.")
    Integer emailVerificationCode;
}
