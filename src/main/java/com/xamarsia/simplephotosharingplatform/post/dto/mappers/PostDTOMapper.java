package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;

import java.util.function.Function;

@Service
public class PostDTOMapper implements Function<Post, PostDTO> {
    @Override
    public PostDTO apply(Post post) {
        String updateDateTime = (post.getUpdateDateTime() == null) ? "" : post.getUpdateDateTime().toString();
        
        return new PostDTO(
                post.getId(),
                post.getCreationDateTime().toString(),
                updateDateTime,
                post.getDescription(),
                post.getUser().getUsername());
    }
}
