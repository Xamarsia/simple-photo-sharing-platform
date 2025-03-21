package com.xamarsia.simplephotosharingplatform.user.dto;

import com.xamarsia.simplephotosharingplatform.user.State;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "Id is required.")
        Long id,

        @NotBlank(message = "Username is required.")
        String username,

        String fullName,

        String description,

        @NotBlank(message = "State is required.")
        @Enumerated(EnumType.STRING)
        State state,

        Boolean isProfileImageExist
) {
}
