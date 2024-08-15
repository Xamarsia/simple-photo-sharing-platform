package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.requests.user.EmailUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.responses.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.user.dto.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.ProfileDTOMapper;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserPreviewDTOMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserDTOMapper userDTOMapper;
    private final UserPreviewDTOMapper userPreviewDTOMapper;
    private final ProfileDTOMapper profileDTOMapper;

    @GetMapping
    public ResponseEntity<UserDTO> getAuthenticatedUser(Authentication authentication) {
        UserDTO userDTO = userDTOMapper.apply(authentication, service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<ProfileDTO> getProfileDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        ProfileDTO profileDTO = profileDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(profileDTO);
    }

    @GetMapping("/preview/{username}")
    public ResponseEntity<UserPreviewDTO> getUserPreviewDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        UserPreviewDTO userPreviewDTO = userPreviewDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userPreviewDTO);
    }

    @GetMapping(value = "{username}/profile/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username) {
        return service.getProfileImage(username);
    }

    @GetMapping("/{username}/followers/page")
    public Page<UserPreviewDTO> getUserFollowersPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followersPage = service.getUserFollowersPage(username, page, size);
        List<UserPreviewDTO> followingsPreviewDTO = followersPage.stream()
                .map(follower -> userPreviewDTOMapper.apply(authentication, follower)).collect(Collectors.toList());
        return new PageImpl<>(followingsPreviewDTO, followersPage.getPageable(), followersPage.getTotalElements());
    }

    @GetMapping("/{username}/followings/page")
    public Page<UserPreviewDTO> getUserFollowingsPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getUserFollowingsPage(username, page, size);
        List<UserPreviewDTO> followingsPreviewDTO = followingsPage.stream()
                .map(following -> userPreviewDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsPreviewDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }

    @GetMapping("/search/page")
    public Page<UserPreviewDTO> searchUserBySubstring(Authentication authentication,
            @RequestParam String substring,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> searchedPage = service.searchUserBySubstring(substring, page, size);

        List<UserPreviewDTO> followingsPreviewDTO = searchedPage.stream()
                .map(following -> userPreviewDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsPreviewDTO, searchedPage.getPageable(), searchedPage.getTotalElements());
    }

    @GetMapping("/{followingUsername}/isUserInFollowing")
    public ResponseEntity<Boolean> isUserInFollowing(Authentication authentication,
            @PathVariable String followingUsername) {
        return ResponseEntity.ok().body(service.isUserInFollowing(authentication, followingUsername));
    }

    @GetMapping("/{username}/following")
    public List<UserPreviewDTO> getUserFollowings(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user.getFollowings().stream().map(following -> userPreviewDTOMapper.apply(authentication, following))
                .collect(Collectors.toList());
    }

    @PutMapping("/{followerUsername}/follow")
    public ResponseEntity<?> addFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        User user = service.follow(authentication, followerUsername);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/{followerUsername}/unfollow")
    public ResponseEntity<?> removeFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        User user = service.unfollow(authentication, followerUsername);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(Authentication authentication,
            @RequestBody @Valid UserUpdateRequest newUserData) {
        User updatedUser = service.updateUser(authentication, newUserData);
        UserDTO userDTO = userDTOMapper.apply(updatedUser, State.CURRENT);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/email/update")
    public ResponseEntity<?> updateUserEmail(Authentication authentication,
            @RequestBody @Valid EmailUpdateRequest newEmailData) {
        User updatedUser = service.updateUserEmail(authentication, newEmailData);
        UserDTO userDTO = userDTOMapper.apply(updatedUser, State.CURRENT);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/username/update")
    public ResponseEntity<?> updateUserUsername(Authentication authentication,
            @RequestBody @Valid UsernameUpdateRequest newUsername) {
        User updatedUser = service.updateUserUsername(authentication, newUsername);
        UserDTO userDTO = userDTOMapper.apply(updatedUser, State.CURRENT);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/password/update")
    public ResponseEntity<?> updateUserPassword(Authentication authentication,
            @RequestBody @Valid PasswordUpdateRequest newPasswordData) {
        User updatedUser = service.updateUserPassword(authentication, newPasswordData);
        UserDTO userDTO = userDTOMapper.apply(updatedUser, State.CURRENT);
        return ResponseEntity.ok()
                .body(userDTO);
    }

    @PutMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        User user = service.getAuthenticatedUser(authentication);
        service.uploadProfileImage(user, file);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @DeleteMapping(value = "/profile/image")
    public ResponseEntity<UserDTO> deleteProfileImage(Authentication authentication) {
        User user = service.deleteProfileImage(authentication);
        UserDTO userDTO = userDTOMapper.apply(user, State.CURRENT);
        return ResponseEntity.ok().body(userDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        service.deleteUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }
}