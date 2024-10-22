package com.xamarsia.simplephotosharingplatform.security.authentication;

import com.xamarsia.simplephotosharingplatform.responses.EmptyJsonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> saveAuthentication(Authentication authentication) {
        authService.saveAuthentication(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @GetMapping("/isUsed")
    public ResponseEntity<?> isAuthenticationUsed(Authentication authentication) {
        Boolean isAuthUsed = authService.isAuthenticationUsed(authentication);
        return ResponseEntity.ok().body(isAuthUsed);
    }
}
