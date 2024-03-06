package com.xamarsia.simplephotosharingplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.xamarsia.simplephotosharingplatform.email.EmailVerificationService;
import com.xamarsia.simplephotosharingplatform.user.*;
import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationRequest;
import com.xamarsia.simplephotosharingplatform.dto.auth.AuthenticationResponse;
import com.xamarsia.simplephotosharingplatform.dto.auth.IsEmailAlreadyInUseRequest;
import com.xamarsia.simplephotosharingplatform.dto.auth.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.security.jwt.JwtService;
import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.security.token.TokenRepository;
import com.xamarsia.simplephotosharingplatform.security.token.TokenType;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTOMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Validated
@AllArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    private final UserPreviewDTOMapper userPreviewDTOMapper;
    private final EmailVerificationService emailVerificationService;

    public Boolean isEmailAlreadyInUse(IsEmailAlreadyInUseRequest request) {
        return userService.isEmailUsed(request.getEmail());
    }

    public UserPreviewDTO register(RegisterRequest registerRequest) {
//        boolean isCodeCorrect = emailVerificationService.isVerificationCodeCorrect(registerRequest.getEmail(),
//                registerRequest.getEmailVerificationCode());
//        if(!isCodeCorrect) {
//            throw new RuntimeException("Register: Email verification failed");
//        }

        User user = User.builder().fullName(registerRequest.getFullName()).username(registerRequest.getUsername()).email(registerRequest.getEmail()).password(passwordEncoder.encode(registerRequest.getPassword())).build();

        User savedUser = userService.saveUser(user);

        MultipartFile file = registerRequest.getImage();
        if (file != null && !file.isEmpty()) {
            userService.uploadProfileImage(user, registerRequest.getImage());
        }
        return userPreviewDTOMapper.apply(savedUser, State.CURRENT);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userService.getByEmail(request.email());
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Login: The password is incorrect!");
        }

        var jwtToken = jwtService.generateToken(user.getId().toString());
        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, userDTO);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(token);
    }

    private void deleteAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllByUser_Id(user.getId());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(storedToken -> {
            tokenRepository.deleteById(storedToken.id);
        });
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(AuthenticationConstants.Validation.BEARER)) {
            return;
        }

        final String refreshToken = authHeader.substring(AuthenticationConstants.Validation.BEARER.length());
        final String userId = jwtService.getSubject(refreshToken);

        User user = userService.getById(Long.parseLong(userId));
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);

        if (jwtService.isTokenValid(refreshToken, userId)) {
            var newToken = jwtService.generateToken(userId);

            deleteAllUserTokens(user);
            saveUserToken(user, newToken);

            var authResponse = new AuthenticationResponse(newToken, userDTO);

            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
