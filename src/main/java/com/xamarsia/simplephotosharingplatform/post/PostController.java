package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.dto.post.UpdatePostRequest;
import com.xamarsia.simplephotosharingplatform.post.dto.DetailedPostDTO;
import com.xamarsia.simplephotosharingplatform.post.dto.PostDTO;
import com.xamarsia.simplephotosharingplatform.post.dto.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.post.dto.mappers.DetailedPostDTOMapper;
import com.xamarsia.simplephotosharingplatform.post.dto.mappers.PostDTOMapper;
import com.xamarsia.simplephotosharingplatform.post.dto.mappers.PostPreviewDTOMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;
    private final PostDTOMapper postDTOMapper;
    private final PostPreviewDTOMapper postPreviewMapper;
    private final DetailedPostDTOMapper detailedPostMapper;

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        Post post = service.getPostById(postId);
        PostDTO postDTO = postDTOMapper.apply(post);

        return ResponseEntity.ok().body(postDTO);
    }

    @GetMapping("/{postId}/image")
    public byte[] getPostImage(@PathVariable Long postId) {
        return service.getPostImage(postId);
    }


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(Authentication authentication,
            @ModelAttribute CreatePostRequest newPost) {
        Post savedPost = service.createPost(authentication, newPost);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @PutMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePostImage(Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @PathVariable Long postId) {
        service.updatePostImage(authentication, file, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @PutMapping("/{postId}/update")
    public ResponseEntity<?> updatePost(Authentication authentication,
            @RequestBody UpdatePostRequest newPost,
            @PathVariable Long postId) {
        Post savedPost = service.updatePost(authentication, newPost, postId);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @GetMapping("preview/{userId}/page")
    public Page<PostPreviewDTO> getPostsPreviewPageByUserId(@PathVariable Long userId,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getPostsPageByUserId(userId, page, size);
        List<PostPreviewDTO> postsPreview = postsPage.stream().map(postPreviewMapper).collect(Collectors.toList());

        return new PageImpl<>(postsPreview, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("following/preview/page")
    public Page<PostPreviewDTO> getPostsPreviewByUserFollowing(Authentication authentication,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getUserFollowingsPostsPage(authentication, size, page);
        List<PostPreviewDTO> postsPreview = postsPage.stream().map(postPreviewMapper).collect(Collectors.toList());
        return new PageImpl<>(postsPreview, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("following/detailed/page")
    public Page<DetailedPostDTO> getDetailedPostsByUserFollowing(Authentication authentication,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getUserFollowingsPostsPage(authentication, size, page);
        List<DetailedPostDTO> detailedPosts = postsPage.stream()
                .map(post -> detailedPostMapper.apply(authentication, post)).collect(Collectors.toList());
        return new PageImpl<>(detailedPosts, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("random/detailed/page")
    public Page<DetailedPostDTO> getDetailedPostsRandomly(Authentication authentication,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getPostsPageRandomly(authentication, size, page);
        List<DetailedPostDTO> detailedPosts = postsPage.stream()
                .map(post -> detailedPostMapper.apply(authentication, post)).collect(Collectors.toList());
        return new PageImpl<>(detailedPosts, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("/{userId}/count")
    public Integer getPostsCountByUserId(@PathVariable Long userId) {
        return service.getPostsCountByUserId(userId);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(Authentication authentication,
            @PathVariable Long postId) {
        service.deletePostById(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }
}
