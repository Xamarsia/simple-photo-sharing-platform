package com.xamarsia.simplephotosharingplatform.user;


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
    public ResponseEntity<UserDTO> getAuthenticatedUser(Authentication authentication){
        UserDTO userDTO = userDTOMapper.apply(service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok()
                .body(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDTOById(@PathVariable Long id) {
        User user = service.getById(id);
        UserDTO userDTO = userDTOMapper.apply(user);
        return ResponseEntity.ok()
                .body(userDTO);
    }

    @GetMapping("/all")
    public CollectionModel<EntityModel<UserDTO>> all() {
        List<EntityModel<UserDTO>> users = service.selectAllUsers().stream().map(user ->  EntityModel.of(
                        userDTOMapper.apply(user),
                        linkTo(methodOn(UserController.class).getUserDTOById(user.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdateRequest newUserData, @PathVariable Long id) {
        User updatedUser = service.updateUser(newUserData, id);
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
