package com.xamarsia.simplephotosharingplatform.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.security.authentication.Auth;
import com.xamarsia.simplephotosharingplatform.security.authentication.AuthService;
import com.xamarsia.simplephotosharingplatform.user.following.FollowingPK;
import com.xamarsia.simplephotosharingplatform.user.following.FollowingService;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    @Mock
    private FollowingService followingService;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService service;

    private final static Long USER_ID = 9845786234L;

    private final static User user = User.builder()
            .id(USER_ID)
            .username("username")
            .fullName("Full Name")
            .build();

    @Test
    void testGetState_CURRENT() {
        Auth auth = Auth.builder()
                .id(null)
                .user(user)
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);

        State state = service.getState(authentication, auth.user.getUsername());

        assertNotNull(state);
        assertEquals(state, State.CURRENT);
    }

    @Test
    void testGetState_FOLLOW() {
        Auth auth = Auth.builder()
                .id(null)
                .user(user)
                .build();

        User follower = User.builder()
                .id(98456867L)
                .username("follower")
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);
        when(repository.findUserByUsername(follower.getUsername())).thenReturn(Optional.ofNullable(follower));
        when(followingService.isUserFollowedBy(new FollowingPK(user, follower))).thenReturn(true);

        State state = service.getState(authentication, follower.getUsername());

        assertNotNull(state);
        assertEquals(state, State.FOLLOW);
    }

    @Test
    void testGetState_NONE() {
        Auth auth = Auth.builder()
                .id(null)
                .user(user)
                .build();

        User unrelatedUser = User.builder()
                .id(98456867L)
                .username("unrelatedUser")
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);
        when(repository.findUserByUsername(unrelatedUser.getUsername()))
                .thenReturn(Optional.ofNullable(unrelatedUser));

        when(followingService.isUserFollowedBy(new FollowingPK(user, unrelatedUser))).thenReturn(false);

        State state = service.getState(authentication, unrelatedUser.getUsername());

        assertNotNull(state);
        assertEquals(state, State.NONE);
    }

    @Test
    void testGetAuthenticatedUser() {
        Auth auth = Auth.builder()
                .id(null)
                .user(user)
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);

        assertDoesNotThrow(() -> {
            User authUser = service.getAuthenticatedUser(authentication);
            assertEquals(user, authUser);
        });
    }

    @Test
    void testGetAuthenticatedUser_Fails() {
        Auth auth = Auth.builder()
                .id(null)
                .user(null)
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);

        assertThrowsExactly(ResourceNotFoundException.class,
                () -> service.getAuthenticatedUser(authentication));
    }

    @Test
    void testSaveUser() {
        when(repository.save(user)).thenReturn(user);
        assertDoesNotThrow(() -> service.saveUser(user));
    }

    @Test
    void testSaveUser_UniqueUsernameConstraintFailed() {
        when(repository.save(user)).thenThrow(new RuntimeException("user_username_unique"));

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.saveUser(user));
        assertEquals(appException.getErrorCode(), ApplicationError.UNIQUE_USERNAME_CONSTRAINT_FAILED);
    }

    @Test
    void testSaveUser_Failed() {
        when(repository.save(user)).thenThrow(new RuntimeException("DB internal error"));

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.saveUser(user));
        assertEquals(appException.getErrorCode(), ApplicationError.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDeleteUser() {
        when(repository.save(user)).thenReturn(user);
        assertDoesNotThrow(() -> service.saveUser(user));
    }

    @Test
    void testGetUserByUsername() {
        when(repository.findUserByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        assertDoesNotThrow(() -> service.getUserByUsername(user.getUsername()));
    }

    @Test
    void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest("username", "Full Name");

        User newUser = User.builder()
                .fullName(registerRequest.fullName())
                .username(registerRequest.username())
                .build();

        Auth auth = Auth.builder()
                .id(null)
                .user(newUser)
                .build();

        when(authService.getAuthentication(authentication)).thenReturn(auth);
        when(authService.saveAuth(auth)).thenReturn(auth);
        when(repository.save(newUser)).thenReturn(newUser);

        User registeredUser = service.register(authentication, registerRequest);

        assertEquals(registeredUser.getUsername(), user.getUsername());
        assertEquals(registeredUser.getFullName(), user.getFullName());
    }
}
