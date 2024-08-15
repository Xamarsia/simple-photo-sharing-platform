package com.xamarsia.simplephotosharingplatform.requests.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Username.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsernameUpdateRequest {
    @NotBlank(message = "Email is required.")
    @UniqueUsername(message = "A user with username '${validatedValue}' already exists.")
    private String username;
}
