package com.xamarsia.simplephotosharingplatform.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.user.dto.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.ProfileDTOMapper;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserService service;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Mock
    private ProfileDTOMapper profileDTOMapper;

    @InjectMocks
    private UserController userController;

    @Mock
    private Authentication authentication;

    private final static Long USER_ID = 9845786234L;

    private final static User user = User.builder()
            .id(USER_ID)
            .username("username")
            .fullName("Full Name")
            .build();

    private final UserDTO userDTO = new UserDTO(USER_ID,
            user.getUsername(),
            user.getFullName(),
            user.getDescription(),
            State.CURRENT,
            user.getIsProfileImageExist());

    private final RegisterRequest registerRequest = new RegisterRequest(
            user.getUsername(),
            user.getFullName());

    private final ProfileDTO profileDTO = new ProfileDTO(
            5,
            10,
            30,
            userDTO);

    private final UserInfoUpdateRequest userInfoUpdateRequest = new UserInfoUpdateRequest(
            null,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

    @Test
    void testGetAuthenticatedUser() {
        when(service.getAuthenticatedUser(authentication)).thenReturn(user);
        when(userDTOMapper.apply(authentication, user)).thenReturn(userDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<UserDTO> response = userController.getAuthenticatedUser(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), userDTO);
        });
    }

    @Test
    void testIsUsernameUsed() {
        Boolean isUsernameUsed = true;
        when(service.isUsernameUsed(user.getUsername())).thenReturn(isUsernameUsed);

        assertDoesNotThrow(() -> {
            ResponseEntity<Boolean> response = userController.isUsernameUsed(user.getUsername());
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), isUsernameUsed);
        });
    }

    @Test
    void testIsRegistered() {
        Boolean isRegistered = false;
        when(service.isRegistered(authentication)).thenReturn(isRegistered);

        assertDoesNotThrow(() -> {
            ResponseEntity<Boolean> response = userController.isRegistered(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), isRegistered);
        });
    }

    @Test
    void testRegister() {
        when(service.register(authentication, registerRequest)).thenReturn(user);
        when(userDTOMapper.apply(authentication, user)).thenReturn(userDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<UserDTO> response = userController.register(authentication, registerRequest);
            assertEquals(response.getStatusCode(), HttpStatus.CREATED);
            assertEquals(response.getBody(), userDTO);
        });
    }

    @Test
    void testGetProfileDTOByUsername() {
        when(service.getUserByUsername(user.getUsername())).thenReturn(user);
        when(profileDTOMapper.apply(authentication, user)).thenReturn(profileDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<ProfileDTO> response = userController.getProfileDTOByUsername(authentication,
                    user.getUsername());
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), profileDTO);
        });
    }

    @Test
    void testAddFollower() {
        String followerUsername = "followerUsername";
        Mockito.doNothing().when(service).follow(authentication, followerUsername);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = userController.addFollower(authentication, followerUsername);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    void testRemoveFollower() {
        String followerUsername = "followerUsername";
        Mockito.doNothing().when(service).deleteFollowing(authentication, followerUsername);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = userController.removeFollower(authentication, followerUsername);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    void testUpdateUserInfo() {
        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .description(userInfoUpdateRequest.description())
                .fullName(userInfoUpdateRequest.fullName())
                .build();

        UserDTO updatedUserDTO = new UserDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getFullName(),
                updatedUser.getDescription(),
                State.CURRENT,
                updatedUser.getIsProfileImageExist());

        when(service.updateUserInfo(authentication, userInfoUpdateRequest)).thenReturn(updatedUser);
        when(userDTOMapper.apply(authentication, updatedUser)).thenReturn(updatedUserDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<UserDTO> response = userController.updateUserInfo(authentication, userInfoUpdateRequest);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), updatedUserDTO);
        });
    }

    @Test
    void testUpdateUsername() {
        UsernameUpdateRequest usernameUpdateRequest = new UsernameUpdateRequest("newUsername");

        User updatedUser = User.builder()
                .id(user.getId())
                .username(usernameUpdateRequest.username())
                .fullName(user.getFullName())
                .description(user.getDescription())
                .fullName(user.getFullName())
                .build();

        UserDTO updatedUserDTO = new UserDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getFullName(),
                updatedUser.getDescription(),
                State.CURRENT,
                updatedUser.getIsProfileImageExist());

        when(service.updateUsername(authentication, usernameUpdateRequest)).thenReturn(updatedUser);
        when(userDTOMapper.apply(authentication, updatedUser)).thenReturn(updatedUserDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<UserDTO> response = userController.updateUsername(authentication, usernameUpdateRequest);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), updatedUserDTO);
        });
    }

    @Test
    void testDeleteProfileImage() {
        when(service.deleteProfileImage(authentication)).thenReturn(user);
        when(userDTOMapper.apply(authentication, user)).thenReturn(userDTO);

        assertDoesNotThrow(() -> {
            ResponseEntity<UserDTO> response = userController.deleteProfileImage(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    void testDeleteUser() {
        Mockito.doNothing().when(service).deleteUser(authentication);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = userController.deleteUser(authentication);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }
}
