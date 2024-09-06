package com.xamarsia.simplephotosharingplatform.user.dto.mappers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.PostService;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.following.FollowingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileDTOMapper {
    private final UserDTOMapper userDTOMapper;
    private final PostService service;
    private final FollowingService followingService;

    public ProfileDTO apply(Authentication authentication, User user) {

        UserDTO userDTO = userDTOMapper.apply(authentication, user);

        return new ProfileDTO(
                followingService.getFollowingsCountByUserId(user.getId()),
                followingService.getFollowersCountByUserId(user.getId()),
                service.getPostsCountByUserId(user.getId()),
                userDTO);
    }
}
