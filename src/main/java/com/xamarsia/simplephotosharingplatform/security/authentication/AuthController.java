package com.xamarsia.simplephotosharingplatform.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<?> createAuth(Authentication authentication) {
        authService.createAuth(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    @GetMapping("/isUsed")
    public ResponseEntity<?> isAuthUsed(Authentication authentication) {
        Boolean isAuthUsed = authService.isAuthUsed(authentication);
        return ResponseEntity.ok().body(isAuthUsed);
    }
}
