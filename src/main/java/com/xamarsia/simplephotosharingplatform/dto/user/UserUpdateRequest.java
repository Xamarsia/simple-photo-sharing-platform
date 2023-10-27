package com.xamarsia.simplephotosharingplatform.dto.user;

import com.xamarsia.simplephotosharingplatform.common.validator.Email.UniqueEmail;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserUpdateRequest {

    //TODO Move the email update logic to a separate request with a request for e-mail confirmation
    @UniqueEmail
    @Size(min = 1, message = "Email is required.")
    private String email;

    @NotNull
    @NotEmpty
    private String fullName;

    @NotNull
    @NotEmpty
    private String username;

    @NotEmpty
    @NotNull(message = "Password is required.")
    @Size(min = 6, message = "Password should be at least 6 characters.")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
