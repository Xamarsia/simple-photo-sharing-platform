package com.xamarsia.simplephotosharingplatform.requests.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailUpdateRequest {
    @NotBlank(message = "Email is required.")
    @UniqueEmail(message = "A user with email '${validatedValue}' already exists.")
    private String email;

    @NotNull(message = "Email verification code is required.")
    private Integer emailVerificationCode;
}
