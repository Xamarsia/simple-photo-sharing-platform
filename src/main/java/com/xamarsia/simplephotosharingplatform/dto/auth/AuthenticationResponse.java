package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationResponse(
                @NotBlank(message = "Token is required.") String token,
                @NotNull(message = "UserDTO is required.") UserDTO userDTO) {
}
