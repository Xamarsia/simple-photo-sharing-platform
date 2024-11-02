package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;
import com.xamarsia.simplephotosharingplatform.post.like.LikeService;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PostDTOMapper implements Function<Post, PostDTO> {
    private final LikeService likeService;

    @Override
    public PostDTO apply(Post post) {
        String updateDateTime = (post.getUpdateDateTime() == null) ? "" : post.getUpdateDateTime().toString();

        return new PostDTO(
                post.getId(),
                post.getCreationDateTime().toString(),
                updateDateTime,
                post.getDescription(),
                post.getUser().getUsername(),
                likeService.getPostLikesCount(post.getId()));
    }
}
