package com.xamarsia.simplephotosharingplatform.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {
    @NotBlank(message = "Old password is required.")
    private String oldPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 6, message = "New password should be at least 6 characters.")
    private String newPassword;
}