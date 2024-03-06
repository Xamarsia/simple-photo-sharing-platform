package com.xamarsia.simplephotosharingplatform.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDTOMapper {
    private final UserService service;

    public UserDTOMapper(UserService service) {
        this.service = service;
    }

    public UserDTO apply(User user, State userState) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                userState
        );
    }

    public UserDTO apply(Authentication authentication, User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),

                service.getState(authentication, user)
        );
    }
}
