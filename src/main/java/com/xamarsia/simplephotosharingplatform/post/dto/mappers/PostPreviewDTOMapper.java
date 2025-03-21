package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.dto.PostPreviewDTO;

@Service
public class PostPreviewDTOMapper implements Function<Post, PostPreviewDTO> {

    @Override
    public PostPreviewDTO apply(Post post) {
        return new PostPreviewDTO(
                post.getId()
        );
    }
}
