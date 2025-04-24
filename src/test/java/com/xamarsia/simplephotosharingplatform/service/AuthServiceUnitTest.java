package com.xamarsia.simplephotosharingplatform.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.xamarsia.simplephotosharingplatform.entity.Auth;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;
import com.xamarsia.simplephotosharingplatform.enums.Role;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.UnauthorizedAccessException;
import com.xamarsia.simplephotosharingplatform.repository.AuthRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock
    private AuthRepository repository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService service;

    private final static String authId = "authentication unique id";

    private final static User user = User.builder()
            .id(24859768L)
            .username("username")
            .fullName("Full Name")
            .build();

    private final static Auth emptyAuth = Auth.builder().build();

    private final static Auth filledAuth = Auth.builder()
            .id(authId)
            .user(user)
            .build();

    private final static Role role = Role.USER;

    private static Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Test
    void testGetAuthentication() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(emptyAuth));

        assertDoesNotThrow(() -> {
            Auth resultedAuth = service.getAuthentication(authentication);
            assertEquals(resultedAuth, emptyAuth);
        });
    }

    @Test
    void testGetAuthentication_UnauthorizedAccessException() {
        AnonymousAuthenticationToken anonymAuthToken = new AnonymousAuthenticationToken(authId, emptyAuth,
                getAuthorities());

        UnauthorizedAccessException appException = assertThrowsExactly(UnauthorizedAccessException.class,
                () -> service.getAuthentication(anonymAuthToken));
        assertEquals(appException.getErrorCode(), ApplicationError.UNAUTHORIZED_ACCESS);
    }

    @Test
    void testGetAuthentication_ResourceNotFoundException() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(null));

        ResourceNotFoundException appException = assertThrowsExactly(ResourceNotFoundException.class,
                () -> service.getAuthentication(authentication));
        assertEquals(appException.getErrorCode(), ApplicationError.RESOURCE_NOT_FOUND);
    }

    @Test
    void testCreateAuth_UnauthorizedAccessException() {
        AnonymousAuthenticationToken anonymAuthToken = new AnonymousAuthenticationToken(authId, emptyAuth,
                getAuthorities());
        UnauthorizedAccessException appException = assertThrowsExactly(UnauthorizedAccessException.class,
                () -> service.createAuth(anonymAuthToken));
        assertEquals(appException.getErrorCode(), ApplicationError.UNAUTHORIZED_ACCESS);
    }

    @Test
    void testCreateAuth_WhenAuthAlreadyExist() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.existsById(authId)).thenReturn(true);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(emptyAuth));

        assertDoesNotThrow(() -> {
            Auth resultedAuth = service.createAuth(authentication);
            assertEquals(resultedAuth, emptyAuth);
        });
    }

    @Test
    void testCreateAuth_UniqueAuthConstraintFailed() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.existsById(authId)).thenReturn(true);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(filledAuth));

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.createAuth(authentication));
        assertEquals(appException.getErrorCode(), ApplicationError.UNIQUE_AUTH_CONSTRAINT_FAILED);
    }

    @Test
    void testCreateAuth() {
        Auth newAuth = Auth.builder()
                .id(authId)
                .build();

        when(authentication.getName()).thenReturn(authId);
        when(repository.existsById(authId)).thenReturn(false);
        when(repository.save(newAuth)).thenReturn(newAuth);

        assertDoesNotThrow(() -> {
            Auth resultedAuth = service.createAuth(authentication);
            assertEquals(resultedAuth, newAuth);
        });
    }

    @Test
    void testSaveAuth_InternalServerError() {
        when(repository.save(emptyAuth)).thenThrow(RuntimeException.class);

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.saveAuth(emptyAuth));
        assertEquals(appException.getErrorCode(), ApplicationError.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testIsAuthUsed_False() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(emptyAuth));

        assertDoesNotThrow(() -> {
            assertEquals(false, service.isAuthUsed(authentication));
        });
    }

    @Test
    void testIsAuthUsed_True() {
        when(authentication.getName()).thenReturn(authId);
        when(repository.findById(authId)).thenReturn(Optional.ofNullable(filledAuth));

        assertDoesNotThrow(() -> {
            assertEquals(true, service.isAuthUsed(authentication));
        });
    }
}
