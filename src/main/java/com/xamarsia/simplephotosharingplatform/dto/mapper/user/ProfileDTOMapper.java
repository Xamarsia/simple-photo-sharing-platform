package com.xamarsia.simplephotosharingplatform.dto.mapper.user;

import java.util.function.BiFunction;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.dto.user.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.dto.user.UserDTO;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.service.FollowingService;
import com.xamarsia.simplephotosharingplatform.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileDTOMapper implements BiFunction<Authentication, User, ProfileDTO> {
    private final UserDTOMapper userDTOMapper;
    private final PostService service;
    private final FollowingService followingService;

    @Override
    public ProfileDTO apply(Authentication authentication, User user) {
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return new ProfileDTO(
                followingService.getFollowingsCountByUserId(user.getId()),
                followingService.getFollowersCountByUserId(user.getId()),
                service.getPostsCountByUserId(user.getId()),
                userDTO);
    }
}
