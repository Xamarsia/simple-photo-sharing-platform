package com.xamarsia.simplephotosharingplatform.user;


import com.xamarsia.simplephotosharingplatform.dto.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserDTOMapper userDTOMapper;
    private final JwtService jwtService;

    @GetMapping("/")
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

    @PutMapping("/updateImage/{id}")
    public void updateProfileImageId(String profileImageId, @PathVariable Long id) {
        service.updateProfileImageId(profileImageId, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        service.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
