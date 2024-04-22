package com.xamarsia.simplephotosharingplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.xamarsia.simplephotosharingplatform.email.EmailVerificationService;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.InternalValidationException;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;


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
    private final Validator validator;

    public Boolean isEmailAlreadyInUse(IsEmailAlreadyInUseRequest request) {
        return userService.isEmailUsed(request.getEmail());
    }

    public UserPreviewDTO register(RegisterRequest registerRequest) {
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
        return userPreviewDTOMapper.apply(savedUser, State.CURRENT);
    }

    private void validateResponse(AuthenticationResponse response){
        Set<ConstraintViolation<AuthenticationResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            throw new InternalValidationException(violations);
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userService.getByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("[Login]: The password is incorrect.");
        }

        String jwtToken = jwtService.generateToken(user.getId().toString());
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString());

        saveUserToken(user, jwtToken);

        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);
        AuthenticationResponse response = new AuthenticationResponse(jwtToken, refreshToken, userDTO);
        validateResponse(response);
        return response;
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(token);
    }

//    private void deleteAllUserTokens(User user) {
//        var validUserTokens = tokenRepository.findAllByUser_Id(user.getId());
//        if (validUserTokens.isEmpty())
//            return;
//
//        validUserTokens.forEach(storedToken -> {
//            tokenRepository.deleteById(storedToken.id);
//        });
//    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }



    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authHeader.substring("Bearer ".length());
        final String userId = jwtService.getSubject(refreshToken);

        User user = userService.getById(Long.parseLong(userId));
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);

        if (jwtService.isTokenValid(refreshToken, userId)) {
            String accessToken = jwtService.generateToken(userId);

            // TODO Revoke users or not
            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);

            AuthenticationResponse authResponse = new AuthenticationResponse(accessToken, refreshToken, userDTO);
            validateResponse(authResponse);
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
