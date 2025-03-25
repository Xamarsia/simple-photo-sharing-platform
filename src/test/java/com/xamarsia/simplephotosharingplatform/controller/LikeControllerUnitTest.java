package com.xamarsia.simplephotosharingplatform.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.entity.Like;
import com.xamarsia.simplephotosharingplatform.entity.Post;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.entity.PK.LikePK;
import com.xamarsia.simplephotosharingplatform.service.LikeService;

@ExtendWith(MockitoExtension.class)
public class LikeControllerUnitTest {
    @Mock
    private LikeService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LikeController likeController;

    private final static Long POST_ID = 3348384L;

    private final static User user = User.builder()
            .username("username")
            .build();

    private final static Post post = Post.builder()
            .id(POST_ID)
            .description("description")
            .user(user)
            .build();

    private final static LikePK likePK = new LikePK(user, post);

    private final static Like like = Like.builder()
            .id(likePK)
            .build();

    @Test
    void testLikeSuccessfully() {
        when(service.like(authentication, POST_ID)).thenReturn(like);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = likeController.like(authentication, POST_ID);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    void testDeleteLikeSuccessfully() {
        Mockito.doNothing().when(service).deleteLike(authentication, POST_ID);

        assertDoesNotThrow(() -> {
            ResponseEntity<EmptyJson> response = likeController.deleteLike(authentication, POST_ID);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
    }
}
