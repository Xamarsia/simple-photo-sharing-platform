package com.xamarsia.simplephotosharingplatform.request.user;

import com.xamarsia.simplephotosharingplatform.validator.username.UniqueUsername;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username is required.") 
    @UniqueUsername(message = "A user with username '${validatedValue}' already exists.") 
    String username,

    String fullName
    ) {
}
