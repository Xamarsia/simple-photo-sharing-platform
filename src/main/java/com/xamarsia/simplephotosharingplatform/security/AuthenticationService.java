package com.xamarsia.simplephotosharingplatform.security;

import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.user.*;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;

    public Boolean isEmailAlreadyInUse(@NotBlank @PathVariable String email) {
        return userService.isEmailUsed(email);
    }

    public Boolean IsUsernameAlreadyInUse(@NotBlank @PathVariable String username) {
        return userService.isUsernameUsed(username);
    }

    public User register(RegisterRequest registerRequest) {

        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();

        User savedUser = userService.saveUser(user);

        MultipartFile file = registerRequest.getImage();
        if (file != null && !file.isEmpty()) {
            userService.uploadProfileImage(user, registerRequest.getImage());
        }
        return savedUser;
    }
}
