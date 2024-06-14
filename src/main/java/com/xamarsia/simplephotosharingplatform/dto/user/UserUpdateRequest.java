package com.xamarsia.simplephotosharingplatform.dto.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import com.xamarsia.simplephotosharingplatform.common.validator.Username.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequest {

    //TODO Move the email update logic to a separate request with a request for e-mail confirmation
    @NotBlank(message = "Email is required.")
    // @UniqueEmail(message = "A user with email '${validatedValue}' already exists.")
    private String email;

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    // @UniqueUsername(message = "A user with username '${validatedValue}' already exists.")
    private String username;
}
