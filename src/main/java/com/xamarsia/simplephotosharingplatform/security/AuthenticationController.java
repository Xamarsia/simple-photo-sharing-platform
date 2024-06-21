package com.xamarsia.simplephotosharingplatform.security;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.auth.*;
import com.xamarsia.simplephotosharingplatform.user.State;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserPreviewDTOMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserPreviewDTOMapper userPreviewDTOMapper;

    @GetMapping("/isEmailAlreadyInUse/{email}")
    public Boolean isEmailAlreadyInUse(@NotBlank @PathVariable String email) {
        return authService.isEmailAlreadyInUse(email);
    }

    @GetMapping("/IsUsernameAlreadyInUse/{username}")
    public Boolean IsUsernameAlreadyInUse(@NotBlank @PathVariable String username) {
        return authService.IsUsernameAlreadyInUse(username);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute RegisterRequest request) {
        User user = authService.register(request);
        UserPreviewDTO userPreviewDto = userPreviewDTOMapper.apply(user, State.CURRENT);

        return ResponseEntity.status(HttpStatus.CREATED).body(userPreviewDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<?> sendVerificationCodeToEmail(@Valid @RequestBody EmailVerificationRequest request) {
        authService.sendVerificationCodeToEmail(request.email());
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
