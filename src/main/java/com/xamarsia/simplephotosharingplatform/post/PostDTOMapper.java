package com.xamarsia.simplephotosharingplatform.post;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PostDTOMapper implements Function<Post, PostDTO> {
    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(
                post.getId(),
                post.getCreationDateTime().toString(),
                post.getDescription(),
                post.getUser().getUsername());
    }
}
