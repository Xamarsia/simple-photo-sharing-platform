package com.xamarsia.simplephotosharingplatform.post;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTOMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetailedPostDTOMapper {

    private final PostDTOMapper postDTOMapper;
    private final UserPreviewDTOMapper userPreviewDTOMapper;
    private final PostService service;;

    public DetailedPostDTO apply(Authentication authentication, Post post) {

        PostDTO postDTO = postDTOMapper.apply(post);
        User author = service.getPostAuthorByUsername(postDTO.username());

        UserPreviewDTO userPreviewDTO = userPreviewDTOMapper.apply(authentication, author);

        return new DetailedPostDTO(
                postDTO,
                userPreviewDTO);
    }
}