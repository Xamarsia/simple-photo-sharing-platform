package com.xamarsia.simplephotosharingplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class IsEmailAlreadyInUseRequest {
    @NotBlank(message = "Email is required.")
    private String email;
}
