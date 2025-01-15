package com.xamarsia.simplephotosharingplatform.post.dto.mappers;

import java.util.function.BiFunction;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.post.PostService;
import com.xamarsia.simplephotosharingplatform.post.dto.DetailedPostDTO;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;
import com.xamarsia.simplephotosharingplatform.post.like.LikeService;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.dto.UserDTO;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;

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

        return new DetailedPostDTO(
                postDTO,
                userDTO,
                likeService.isPostLiked(authentication, post));
    }
}
