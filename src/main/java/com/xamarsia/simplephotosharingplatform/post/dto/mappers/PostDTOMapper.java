package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;
import com.xamarsia.simplephotosharingplatform.post.like.LikeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostDTOMapper implements Function<Post, PostDTO> {
    private final LikeService likeService;

    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(
                post.getId(),
                post.getCreationDateTime().toString(),
                Objects.toString(post.getUpdateDateTime(), null),
                post.getDescription(),
                post.getUser().getUsername(),
                likeService.getPostLikesCount(post.getId()));
    }
}
