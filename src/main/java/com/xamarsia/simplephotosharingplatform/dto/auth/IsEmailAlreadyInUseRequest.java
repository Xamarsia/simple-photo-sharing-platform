package com.xamarsia.simplephotosharingplatform.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class IsEmailAlreadyInUseRequest {
    @NotEmpty
    @NotNull(message = "Email is required.")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
