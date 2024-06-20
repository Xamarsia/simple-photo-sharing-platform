package com.xamarsia.simplephotosharingplatform.user.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

import com.xamarsia.simplephotosharingplatform.user.State;

public record UserDTO(
        @NotBlank(message = "Id is required.")
        Long id,

        @NotBlank(message = "Full name is required.")
        String fullName,

        @NotBlank(message = "Username is required.")
        String username,

        @NotBlank(message = "Email is required.")
        String email,
        
        String description,

        @NotBlank(message = "Roles is required.")
        List<String> roles,

        @NotBlank(message = "State is required.")
        @Enumerated(EnumType.STRING)
        State state,

        Boolean isProfileImageExist
) {
}