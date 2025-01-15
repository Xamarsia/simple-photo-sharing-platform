package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UsernameUpdateRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserDTOMapper userDTOMapper;
    private final ProfileDTOMapper profileDTOMapper;

    @GetMapping("")
    public ResponseEntity<UserDTO> getAuthenticatedUser(Authentication authentication) {
        UserDTO userDTO = userDTOMapper.apply(authentication, service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/isUsernameUsed/{username}")
    public ResponseEntity<Boolean> isUsernameUsed(@PathVariable String username) {
        Boolean isUsernameUsed = service.isUsernameUsed(username);
        return ResponseEntity.ok().body(isUsernameUsed);
    }

    @GetMapping("/isRegistered")
    public ResponseEntity<Boolean> isRegistered(Authentication authentication) {
        Boolean isRegistered = service.isRegistered(authentication);
        return ResponseEntity.ok().body(isRegistered);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(Authentication authentication,
            @RequestBody @Valid RegisterRequest request) {
        User user = service.register(authentication, request);
        UserDTO userDto = userDTOMapper.apply(authentication, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<ProfileDTO> getProfileDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        ProfileDTO profileDTO = profileDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(profileDTO);
    }

    @GetMapping(value = "{username}/image", produces = MediaType.IMAGE_JPEG_VALUE)
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

    @GetMapping("/{postId}/liked")
    public Page<UserDTO> getUsersLikedPostPage(Authentication authentication,
            @PathVariable Long postId,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getUsersLikedPostPage(postId, page, size);
        List<UserDTO> followingsDTO = followingsPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUser(Authentication authentication,
            @RequestParam String request,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> searchedPage = service.searchUser(request, page, size);

        List<UserDTO> followingsDTO = searchedPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, searchedPage.getPageable(), searchedPage.getTotalElements());
    }

    @PutMapping("/follow/{followerUsername}")
    public ResponseEntity<?> addFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.follow(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    @PutMapping("/deleteFollowing/{followerUsername}")
    public ResponseEntity<?> removeFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.deleteFollowing(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    @PutMapping("/updateUserInfo")
    public ResponseEntity<?> updateUserInfo(Authentication authentication,
            @RequestBody @Valid UserInfoUpdateRequest newUserData) {
        User updatedUser = service.updateUserInfo(authentication, newUserData);
        UserDTO userDTO = userDTOMapper.apply(authentication, updatedUser);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/updateUsername")
    public ResponseEntity<?> updateUsername(Authentication authentication,
            @RequestBody @Valid UsernameUpdateRequest newUsername) {
        User updatedUser = service.updateUsername(authentication, newUsername);
        UserDTO userDTO = userDTOMapper.apply(authentication, updatedUser);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        User user = service.getAuthenticatedUser(authentication);
        service.uploadProfileImage(user, file);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    @DeleteMapping("/image")
    public ResponseEntity<UserDTO> deleteProfileImage(Authentication authentication) {
        User user = service.deleteProfileImage(authentication);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userDTO);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        service.deleteUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }
}
