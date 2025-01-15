package com.xamarsia.simplephotosharingplatform.user.dto.mappers;

import java.util.function.BiFunction;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDTOMapper implements BiFunction<Authentication, User, UserDTO> {
    private final UserService service;

    @Override
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
