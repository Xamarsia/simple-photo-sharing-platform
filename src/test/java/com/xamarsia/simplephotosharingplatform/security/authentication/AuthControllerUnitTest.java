package com.xamarsia.simplephotosharingplatform.security.authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {
    @Mock
    private Authentication authentication;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final static Auth auth = Auth.builder()
            .id(null)
            .user(null)
            .build();

    @Test
    void testCreateAuth() {
        when(authService.createAuth(authentication)).thenReturn(auth);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = authController.createAuth(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    void testIsAuthUsed() {
        Boolean isAuthUsed = true;
        when(authService.isAuthUsed(authentication)).thenReturn(isAuthUsed);

        assertDoesNotThrow(() -> {
            ResponseEntity<Boolean> response = authController.isAuthUsed(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), isAuthUsed);
        });
    }
}
