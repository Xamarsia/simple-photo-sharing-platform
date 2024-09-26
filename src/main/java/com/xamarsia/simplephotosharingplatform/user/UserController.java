package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.responses.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.user.dto.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.ProfileDTOMapper;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;

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
    private final ProfileDTOMapper profileDTOMapper;

    @GetMapping
    public ResponseEntity<UserDTO> getAuthenticatedUser(Authentication authentication) {
        UserDTO userDTO = userDTOMapper.apply(authentication, service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/isUsernameAlreadyInUse/{username}")
    public ResponseEntity<Boolean> isUsernameAlreadyInUse(@PathVariable String username) {
        Boolean isUsernameUsed = service.isUsernameUsed(username);
        return ResponseEntity.ok().body(isUsernameUsed);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(Authentication authentication, 
    @RequestBody @Valid RegisterRequest request) {
        User user = service.register(authentication, request);
        UserDTO userDto = userDTOMapper.apply(user, State.CURRENT);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
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

    @GetMapping(value = "{username}/profile/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username) {
        return service.getProfileImage(username);
    }

    @GetMapping("/{username}/followers")
    public Page<UserDTO> getUserFollowersPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followersPage = service.getUserFollowersPage(username, page, size);
        List<UserDTO> followingsDTO = followersPage.stream()
                .map(follower -> userDTOMapper.apply(authentication, follower)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followersPage.getPageable(), followersPage.getTotalElements());
    }

    @GetMapping("/{username}/followings")
    public Page<UserDTO> getUserFollowingsPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getUserFollowingsPage(username, page, size);
        List<UserDTO> followingsDTO = followingsPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }


    @GetMapping("/{postId}/likers")
    public Page<UserDTO> getPostLikersPage(Authentication authentication,
            @PathVariable Long postId,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getPostLikersPage(postId, page, size);
        List<UserDTO> followingsDTO = followingsPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUserBySubstring(Authentication authentication,
            @RequestParam String substring,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> searchedPage = service.searchUserBySubstring(substring, page, size);

        List<UserDTO> followingsDTO = searchedPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, searchedPage.getPageable(), searchedPage.getTotalElements());
    }

    @GetMapping("/{followingUsername}/isUserInFollowing")
    public ResponseEntity<Boolean> isUserInFollowing(Authentication authentication,
            @PathVariable String followingUsername) {
        return ResponseEntity.ok().body(service.isUserInFollowing(authentication, followingUsername));
    }

    @PutMapping("/{followerUsername}/follow")
    public ResponseEntity<?> addFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.follow(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PutMapping("/{followerUsername}/unfollow")
    public ResponseEntity<?> removeFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.unfollow(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(Authentication authentication,
            @RequestBody @Valid UserInfoUpdateRequest newUserData) {
        User updatedUser = service.updateUser(authentication, newUserData);
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
