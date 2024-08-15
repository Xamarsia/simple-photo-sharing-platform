package com.xamarsia.simplephotosharingplatform.user.dto.mappers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileDTOMapper {
    private final UserPreviewDTOMapper userPreviewDTOMapper;

    public ProfileDTO apply(Authentication authentication, User user) {

        UserPreviewDTO userPreviewDTO = userPreviewDTOMapper.apply(authentication, user);

        return new ProfileDTO(
                user.getFollowings().size(),
                user.getFollowers().size(),
                user.getPosts().size(),
                user.getDescription(),
                userPreviewDTO);
    }
}
