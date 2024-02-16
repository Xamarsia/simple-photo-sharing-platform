package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
        UserDTO userDTO = userDTOMapper.apply(service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserDTOByUsername(@PathVariable String username) {
        User user = service.getByUsername(username);
        UserDTO userDTO = userDTOMapper.apply(user);
        return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/all")
    public List<UserDTO> all() {
        return service.selectAllUsers().stream().map(userDTOMapper)
                .collect(Collectors.toList());
    }

    @GetMapping("preview/all")
    public List<UserPreviewDTO> getPostsPreview() {
        return service.selectAllUsers().stream().map(userPreviewDTOMapper)
                .collect(Collectors.toList());
    }

    @PutMapping("/update")
    ResponseEntity<?> updateUser(Authentication authentication, @RequestBody @Valid UserUpdateRequest newUserData) {
        User updatedUser = service.updateUser(authentication, newUserData);
        UserDTO userDTO = userDTOMapper.apply(updatedUser);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping("/password/update")
    ResponseEntity<?> updateUserPassword(Authentication authentication,
                                 @RequestBody @Valid PasswordUpdateRequest newPasswordData) {
        User updatedUser = service.updateUserPassword(authentication, newPasswordData);
        UserDTO userDTO = userDTOMapper.apply(updatedUser);
        return ResponseEntity.ok()
                .body(userDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        service.deleteUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PostMapping(value = "/profile/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        User user = service.getAuthenticatedUser(authentication);
        service.uploadProfileImage(user, file);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @GetMapping(value = "{username}/profile/image",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username) {
        return service.getProfileImage(username);
    }
}