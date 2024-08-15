package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.PostService;
import com.xamarsia.simplephotosharingplatform.post.dto.DetailedPostDTO;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.UserPreviewDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserPreviewDTOMapper;

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