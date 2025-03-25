package com.xamarsia.simplephotosharingplatform.dto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.dto.mapper.user.ProfileDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.mapper.user.UserDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.user.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.dto.user.UserDTO;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.enums.State;
import com.xamarsia.simplephotosharingplatform.service.FollowingService;
import com.xamarsia.simplephotosharingplatform.service.PostService;

@ExtendWith(MockitoExtension.class)
public class ProfileDTOUnitTest {
    @Mock
    private PostService service;

    @Mock
    private Authentication authentication;

    @Mock
    private FollowingService followingService;

    @Mock
    private UserDTOMapper userDTOMapper;

    private final static Long USER_ID = 9845786234L;
    private final static Integer POSTS_COUNT = 6;
    private final static Integer FOLLOWERS_COUNT = 32;
    private final static Integer FOLLOWINGS_COUNT = 25;

    private final static User user = User.builder()
            .id(USER_ID)
            .username("username")
            .fullName("Full Name")
            .build();

    private final static UserDTO userDTO = new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getDescription(),
            State.NONE,
            user.getIsProfileImageExist());

    @Test
    public void testConvertUserToProfileDTO() {
        when(userDTOMapper.apply(authentication, user)).thenReturn(userDTO);
        when(service.getPostsCountByUserId(USER_ID)).thenReturn(POSTS_COUNT);
        when(followingService.getFollowingsCountByUserId(USER_ID)).thenReturn(FOLLOWINGS_COUNT);
        when(followingService.getFollowersCountByUserId(USER_ID)).thenReturn(FOLLOWERS_COUNT);

        assertDoesNotThrow(() -> {
            ProfileDTOMapper mapper = new ProfileDTOMapper(userDTOMapper, service, followingService);
            ProfileDTO profileDTO = mapper.apply(authentication, user);

            assertEquals(profileDTO.followersCount(), FOLLOWERS_COUNT);
            assertEquals(profileDTO.followingsCount(), FOLLOWINGS_COUNT);
            assertEquals(profileDTO.postsCount(), POSTS_COUNT);
            assertEquals(profileDTO.userDTO(), userDTO);
        });
    }
}
