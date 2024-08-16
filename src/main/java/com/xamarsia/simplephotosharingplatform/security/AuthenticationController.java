package com.xamarsia.simplephotosharingplatform.security;

import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.user.State;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserPreviewDTOMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
