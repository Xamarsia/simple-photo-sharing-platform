package com.xamarsia.simplephotosharingplatform.user.dto.mappers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.user.State;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;

@Service
public class UserDTOMapper {
    private final UserService service;

    public UserDTOMapper(UserService service) {
        this.service = service;
    }

    public UserDTO apply(User user, State userState) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getDescription(),
                userState,
                user.getIsProfileImageExist());
    }

    public UserDTO apply(Authentication authentication, User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getDescription(),
                service.getState(authentication, user.getUsername()),
                user.getIsProfileImageExist());
    }
}
