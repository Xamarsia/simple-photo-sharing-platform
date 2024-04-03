package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.user.UserDTO;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationResponse(
        @NotBlank String token,
        @NotBlank UserDTO userDTO) {
}
