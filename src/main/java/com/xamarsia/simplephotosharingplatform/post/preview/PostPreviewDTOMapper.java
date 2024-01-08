package com.xamarsia.simplephotosharingplatform.post.preview;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.preview.PostPreviewDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PostPreviewDTOMapper implements Function<Post, PostPreviewDTO> {
    @Override
    public PostPreviewDTO apply(Post post) {
        return new PostPreviewDTO(
                post.getId(),
                post.getImageUrl()
        );
    }
}