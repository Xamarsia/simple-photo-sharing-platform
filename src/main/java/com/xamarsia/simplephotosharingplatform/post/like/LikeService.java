package com.xamarsia.simplephotosharingplatform.post.like;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.PostService;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LikeService {
    private final PostService postService;
    private final UserService userService;
    private final LikeRepository repository;

    public Like like(Authentication authentication, Long postId) {
        Post post = postService.getPostById(postId);
        User user = userService.getAuthenticatedUser(authentication);

        LikePK likePK = new LikePK(user.getId(), post.getId());
        boolean isPostLiked = isPostLiked(likePK);

        if (isPostLiked) {
            throw new IllegalArgumentException(
                    "[Like]: The user has already marked this post as liked. It is not possible to like the same post twice");
        }

        Like like = Like.builder()
                .id(likePK)
                .user(user)
                .post(post)
                .build();

        return saveLike(like);
    }

    public void unlike(Authentication authentication, Long postId) throws IllegalArgumentException { //deleteLike
        Post post = postService.getPostById(postId);
        User user = userService.getAuthenticatedUser(authentication);

        LikePK likePK = new LikePK(user.getId(), post.getId());
        boolean isPostLiked = isPostLiked(likePK);

        if (!isPostLiked) {
            throw new IllegalArgumentException("[Unlike]: Post not liked");
        }

        deleteLikeById(likePK);
    }

    @Transactional(readOnly = true)
    public LikeState getPostLikedState(Authentication authentication, Post post) {
        User user = userService.getAuthenticatedUser(authentication);
        LikePK likePK = new LikePK(user.getId(), post.getId());
        boolean isPostLiked = isPostLiked(likePK);

        return isPostLiked ? LikeState.LIKED : LikeState.UNLIKED;
    }

    @Transactional(readOnly = true)
    public Integer getPostLikesCount(Long postId) {
        return repository.countAllByPostId(postId);
    }

    @Transactional(readOnly = true)
    private boolean isPostLiked(LikePK likePK) {
        return repository.existsById(likePK);
    }

    private Like saveLike(Like like) {
        try {
            return repository.save(like);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveLike]: " + e.getMessage());
        }
    }

    private void deleteLikeById(LikePK likePK) {
        try {
            repository.deleteById(likePK);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[DeleteLike]: " + e.getMessage());
        }
    }
}
