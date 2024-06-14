package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTOMapper;
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

    @GetMapping("/preview/{username}")
    public ResponseEntity<UserPreviewDTO> getUserPreviewDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        UserPreviewDTO userPreviewDTO = userPreviewDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userPreviewDTO);
    }

    @GetMapping("/{username}/followers")
    public List<UserPreviewDTO> getUserFollowers(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user.getFollowers().stream().map(follower -> userPreviewDTOMapper.apply(authentication, follower))
                .collect(Collectors.toList());
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

    @GetMapping("/{username}/following/count")
    public Integer getUserFollowingCount(@PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user.getFollowings().size();
    }

    @GetMapping("/{username}/posts/count")
    public Integer getUserPostsCount(@PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user.getPosts().size();
    }

    @GetMapping("/{username}/followers/count")
    public Integer getUserFollowersCount(@PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user.getFollowers().size();
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

    @GetMapping("/all")
    public List<UserDTO> all(Authentication authentication) {
        return service.selectAllUsers().stream().map(user -> userDTOMapper.apply(authentication, user))
                .collect(Collectors.toList());
    }

    @GetMapping("preview/all")
    public List<UserPreviewDTO> getUsersPreview(Authentication authentication) {
        return service.selectAllUsers().stream().map(user -> userPreviewDTOMapper.apply(authentication, user))
                .collect(Collectors.toList());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(Authentication authentication, @RequestBody @Valid UserUpdateRequest newUserData) {
        User updatedUser = service.updateUser(authentication, newUserData);
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

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        service.deleteUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PostMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        User user = service.getAuthenticatedUser(authentication);
        service.uploadProfileImage(user, file);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @GetMapping(value = "{username}/profile/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username) {
        return service.getProfileImage(username);
    }
}