package com.xamarsia.simplephotosharingplatform.dto.mapper.post;

import java.util.function.BiFunction;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.dto.mapper.user.UserDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.post.DetailedPostDTO;
import com.xamarsia.simplephotosharingplatform.dto.post.PostDTO;
import com.xamarsia.simplephotosharingplatform.dto.user.UserDTO;
import com.xamarsia.simplephotosharingplatform.entity.Post;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.enums.LikeState;
import com.xamarsia.simplephotosharingplatform.enums.State;
import com.xamarsia.simplephotosharingplatform.service.LikeService;
import com.xamarsia.simplephotosharingplatform.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetailedPostDTOMapper implements BiFunction<Authentication, Post, DetailedPostDTO> {
    private final PostDTOMapper postDTOMapper;
    private final UserDTOMapper userDTOMapper;
    private final PostService postService;
    private final LikeService likeService;

    @Override
    public DetailedPostDTO apply(Authentication authentication, Post post) {
        PostDTO postDTO = postDTOMapper.apply(post);
        User author = postService.getPostAuthorByUsername(postDTO.username());

        UserDTO userDTO = userDTOMapper.apply(authentication, author);
        LikeState likeState = userDTO.state() == State.UNDEFINED ? null : likeService.isPostLiked(authentication, post);

        return new DetailedPostDTO(
                postDTO,
                userDTO,
                likeState);
    }
}
