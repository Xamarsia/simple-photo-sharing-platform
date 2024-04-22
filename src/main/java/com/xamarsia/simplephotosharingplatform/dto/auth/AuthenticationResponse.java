package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.user.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationResponse(
        @NotBlank(message = "Access token is required.")
        String accessToken,
        @NotBlank(message = "Refresh token is required.")
        String refreshToken,
        @NotNull(message = "UserDTO is required.")
        UserDTO userDTO) {       
}
