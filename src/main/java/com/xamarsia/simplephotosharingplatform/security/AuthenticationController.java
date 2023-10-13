package com.xamarsia.simplephotosharingplatform.security;


import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationRequest;
import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationResponse;
import com.xamarsia.simplephotosharingplatform.dto.auth.IsEmailAlreadyInUseRequest;
import com.xamarsia.simplephotosharingplatform.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Validated
@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;


    @PostMapping("/isEmailAlreadyInUse")
    public Boolean isEmailAlreadyInUse(
            @Valid @RequestBody IsEmailAlreadyInUseRequest request)
    {
        return authenticationService.isEmailAlreadyInUse(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request)
    {
        authenticationService.register(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login( @Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, response.token())
                .body(response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
