package com.xamarsia.simplephotosharingplatform.dto.auth;

import com.xamarsia.simplephotosharingplatform.user.UserDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthenticationResponse(
        @NotNull @NotEmpty String token,
        @NotNull @NotEmpty UserDTO userDTO) {
}
