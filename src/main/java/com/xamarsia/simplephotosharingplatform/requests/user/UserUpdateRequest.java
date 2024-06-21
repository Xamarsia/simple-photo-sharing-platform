package com.xamarsia.simplephotosharingplatform.requests.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequest {
    @NotBlank(message = "Full name is required.")
    private String fullName;

    private String description;
}
