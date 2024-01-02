package com.xamarsia.simplephotosharingplatform.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {

    @NotEmpty
    @NotNull(message = "Old password is required.")
    private String oldPassword;

    @NotEmpty
    @NotNull(message = "New password is required.")
    @Size(min = 6, message = "New password should be at least 6 characters.")
    private String newPassword;
}