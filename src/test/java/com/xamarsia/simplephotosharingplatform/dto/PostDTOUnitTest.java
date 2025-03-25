package com.xamarsia.simplephotosharingplatform.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.dto.mapper.post.PostDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.mapper.post.PostPreviewDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.post.PostDTO;
import com.xamarsia.simplephotosharingplatform.dto.post.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.entity.Post;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.service.LikeService;

@ExtendWith(MockitoExtension.class)
public class PostDTOUnitTest {

    @Mock
    private LikeService likeService;

    @Mock
    private Authentication authentication;

    private final static User user = User.builder()
            .username("username")
            .build();

    private final static Long POST_ID = 3348384L;

    private final static Post post = Post.builder()
            .id(POST_ID)
            .description("description")
            .user(user)
            .build();

    @Test
    public void testConvertPostToPostDTO() {
        when(likeService.getPostLikesCount(post.getId())).thenReturn(5);
        PostDTOMapper postDTOMapper = new PostDTOMapper(likeService);
        PostDTO postDto = postDTOMapper.apply(post);

        assertEquals(post.getId(), postDto.id());
        assertEquals(post.getCreationDateTime().toString(), postDto.createdDate());
        assertEquals(Objects.toString(post.getUpdateDateTime(), null), postDto.updateDateTime());
        assertEquals(post.getDescription(), postDto.description());
        assertEquals(post.getUser().getUsername(), postDto.username());
    }

    @Test
    public void testConvertPostToPostPreviewDTO() {
        PostPreviewDTOMapper postPreviewDTOMapper = new PostPreviewDTOMapper();
        PostPreviewDTO postPreviewDto = postPreviewDTOMapper.apply(post);

        assertEquals(post.getId(), postPreviewDto.id());
    }
}
