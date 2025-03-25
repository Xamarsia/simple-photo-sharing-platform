package com.xamarsia.simplephotosharingplatform.dto.mapper.post;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.dto.post.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.entity.Post;

@Service
public class PostPreviewDTOMapper implements Function<Post, PostPreviewDTO> {

    @Override
    public PostPreviewDTO apply(Post post) {
        return new PostPreviewDTO(
                post.getId()
        );
    }
}
