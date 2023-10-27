package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        return userDTO;
    }
}
