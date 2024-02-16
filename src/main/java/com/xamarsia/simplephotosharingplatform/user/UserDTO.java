package com.xamarsia.simplephotosharingplatform.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UserDTO (
        @NotEmpty
        @NotNull
        Long id,

        @NotEmpty
        @NotNull
        String fullName,

        @NotEmpty
        @NotNull
        String username,

        @NotEmpty
        @NotNull
        String email,

        @NotEmpty
        @NotNull
        List<String> roles) {
}