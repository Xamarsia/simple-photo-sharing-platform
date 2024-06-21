package com.xamarsia.simplephotosharingplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.xamarsia.simplephotosharingplatform.email.EmailVerificationService;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.InternalValidationException;
// import com.xamarsia.simplephotosharingplatform.exception.exceptions.InvalidEmailVerification;
import com.xamarsia.simplephotosharingplatform.user.*;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationRequest;
import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationResponse;
import com.xamarsia.simplephotosharingplatform.dto.auth.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@Validated
@AllArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;

    private final EmailVerificationService emailVerificationService;
    private final Validator validator;

    public Boolean isEmailAlreadyInUse(@NotBlank @PathVariable String email) {
        return userService.isEmailUsed(email);
    }

    public Boolean IsUsernameAlreadyInUse(@NotBlank @PathVariable String username) {
        return userService.isUsernameUsed(username);
    }

    public void sendVerificationCodeToEmail(String email) {
        emailVerificationService.sendEmailVerificationCode(email);
    }

    public User register(RegisterRequest registerRequest) {
        // boolean isCodeCorrect =
        // emailVerificationService.isVerificationCodeCorrect(registerRequest.getEmail(),
        // registerRequest.getEmailVerificationCode());
        // if (!isCodeCorrect) {
        // throw new InvalidEmailVerification("[Register]: Email verification failed.");
        // }

        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        User savedUser = userService.saveUser(user);

        MultipartFile file = registerRequest.getImage();
        if (file != null && !file.isEmpty()) {
            userService.uploadProfileImage(user, registerRequest.getImage());
        }
        return savedUser;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userService.findUserByEmail(request.email());
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("[Login]: The password is incorrect.");
        }

        String jwtToken = jwtService.generateToken(user.getId().toString());
        jwtService.saveUserToken(user, jwtToken);

        AuthenticationResponse response = new AuthenticationResponse(jwtToken, userDTO);
        Set<ConstraintViolation<AuthenticationResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            throw new InternalValidationException(violations);
        }
        return response;
    }

    private void deleteAllUserTokens(Long userId) {
        jwtService.deleteAllUserTokens(userId);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authHeader.substring("Bearer ".length());
        final String userId = jwtService.getSubject(refreshToken);

        User user = userService.getById(Long.parseLong(userId));
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);

        if (jwtService.isTokenValid(refreshToken, userId)) {
            var newToken = jwtService.generateToken(userId);

            deleteAllUserTokens(user.getId());
            jwtService.saveUserToken(user, newToken);

            var authResponse = new AuthenticationResponse(newToken, userDTO);
            Set<ConstraintViolation<AuthenticationResponse>> violations = validator.validate(authResponse);
            if (!violations.isEmpty()) {
                throw new InternalValidationException(violations);
            }
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
