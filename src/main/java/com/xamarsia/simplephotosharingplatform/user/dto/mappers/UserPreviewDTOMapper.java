package com.xamarsia.simplephotosharingplatform.user.dto.mappers;

import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.State;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserPreviewDTOMapper {
    private final UserService service;

    public UserPreviewDTOMapper(UserService service) {
        this.service = service;
    }

    public UserPreviewDTO apply(User user, State userState) {
        return new UserPreviewDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                userState,
                user.getIsProfileImageExist());
    }

    public UserPreviewDTO apply(Authentication authentication, User user) {
        return new UserPreviewDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                service.getState(authentication, user.getUsername()),
                user.getIsProfileImageExist());
    }
}