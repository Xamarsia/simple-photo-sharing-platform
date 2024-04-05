package com.xamarsia.simplephotosharingplatform.security;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.auth.*;
import com.xamarsia.simplephotosharingplatform.email.EmailVerificationService;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/isEmailAlreadyInUse")
    public Boolean isEmailAlreadyInUse(@Valid @RequestBody IsEmailAlreadyInUseRequest request) {
        return authService.isEmailAlreadyInUse(request);
    }

    @PostMapping(value ="/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute RegisterRequest request) {
        UserPreviewDTO userPreviewDto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPreviewDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, response.token()).body(response);
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<?> sendVerificationCodeToEmail(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendEmailVerificationCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
