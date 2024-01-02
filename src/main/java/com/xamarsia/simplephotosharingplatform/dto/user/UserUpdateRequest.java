package com.xamarsia.simplephotosharingplatform.dto.user;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    //TODO Move the email update logic to a separate request with a request for e-mail confirmation
    @NotNull
    @NotEmpty
    @Size(min = 1, message = "Email is required.")
    private String email;

    @NotNull
    @NotEmpty
    private String fullName;

    @NotNull
    @NotEmpty
    private String username;
}
