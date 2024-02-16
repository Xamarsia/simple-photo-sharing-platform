package com.xamarsia.simplephotosharingplatform.user.preview;


import com.xamarsia.simplephotosharingplatform.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
public class UserPreviewDTOMapper implements Function<User, UserPreviewDTO> {
    @Override
    public UserPreviewDTO apply(User user) {
        return new UserPreviewDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername()
        );
    }
}