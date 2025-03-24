package com.xamarsia.simplephotosharingplatform.post.like;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.PostService;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;

@ExtendWith(MockitoExtension.class)
public class LikeServiceUnitTest {
    @Mock
    private LikeRepository repository;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LikeService service;

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
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(false);
        when(repository.save(like)).thenReturn(like);

        assertDoesNotThrow(() -> {
            Like savedlike = service.like(authentication, POST_ID);
            assertEquals(likePK, savedlike.getId());
        });
    }

    @Test
    void testLikeWithDBException() {
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(false);
        when(repository.save(like)).thenThrow(new IllegalArgumentException());

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.like(authentication, POST_ID));
        assertEquals(appException.getErrorCode(), ApplicationError.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testLikePostBySameUserTwice() {
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(true);

        assertThrowsExactly(IllegalArgumentException.class, () -> service.like(authentication, POST_ID));
    }

    @Test
    void testIsPostLikedForLikedPost() {
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(true);

        LikeState likeState = service.isPostLiked(authentication, post);

        assertEquals(LikeState.LIKE, likeState);
    }

    @Test
    void testIsPostLikedForNotLikedPost() {
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(false);

        LikeState likeState = service.isPostLiked(authentication, post);

        assertEquals(LikeState.NONE, likeState);
    }

    @Test
    void testDeleteLikeSuccessfully() {
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(true);

        Mockito.doNothing().when(repository).deleteById(likePK);

        assertDoesNotThrow(() -> {
            service.deleteLike(authentication, POST_ID);
        });
    }

    @Test
    void testDeleteLikeWithDBException() {
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(true);

        Mockito.doThrow(new IllegalArgumentException()).when(repository).deleteById(likePK);

        ApplicationException appException = assertThrowsExactly(ApplicationException.class,
                () -> service.deleteLike(authentication, POST_ID));
        assertEquals(appException.getErrorCode(), ApplicationError.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDeleteLikeFromNotLikedPost() {
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(userService.getAuthenticatedUser(authentication)).thenReturn(user);
        when(repository.existsById(likePK)).thenReturn(false);

        IllegalArgumentException appException = assertThrowsExactly(IllegalArgumentException.class,
                () -> service.deleteLike(authentication, POST_ID));
        assertEquals(appException.getMessage(), "[DeleteLike]: Post not liked");
    }
}
