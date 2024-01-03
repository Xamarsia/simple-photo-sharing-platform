package com.xamarsia.simplephotosharingplatform.post;

import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
public class PostDTOMapper implements Function<Post, PostDTO> {
    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(
                post.getId(),
                post.getCreatedDate().toString(),
                post.getImageUrl(),
                post.getDescription(),
                post.getUser().getUsername()
        );
    }
}
