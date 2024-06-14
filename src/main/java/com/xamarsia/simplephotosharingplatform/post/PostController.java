package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.dto.EmptyJsonResponse;
import com.xamarsia.simplephotosharingplatform.dto.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.dto.post.UpdatePostRequest;
import com.xamarsia.simplephotosharingplatform.post.preview.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.post.preview.PostPreviewDTOMapper;
import com.xamarsia.simplephotosharingplatform.user.preview.UserPreviewDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/{postId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPostImage(@PathVariable Long postId) {
        return service.getPostImage(postId);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(Authentication authentication,
                                        @ModelAttribute CreatePostRequest newPost) {
        Post savedPost = service.savePost(authentication, newPost);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @PutMapping(value = "/{postId}/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(Authentication authentication,
                                        @ModelAttribute UpdatePostRequest newPost,
                                        @PathVariable Long postId) {
        Post savedPost = service.updatePost(authentication, newPost, postId);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(Authentication authentication,
                                        @PathVariable Long postId) {
        service.deletePostById(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJsonResponse());
    }

    @GetMapping("/{userId}/all")
    public List<PostDTO> getPostsByUserId(@PathVariable Long userId) {
        return service.findPostsByUserId(userId).stream().map(postDTOMapper).collect(Collectors.toList());
    }

    @GetMapping("preview/{userId}/all")
    public List<PostPreviewDTO> getPostsPreviewByUserId(@PathVariable Long userId) {
        return service.findPostsByUserId(userId).stream().map(postPreviewMapper).collect(Collectors.toList());
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
        List<DetailedPostDTO> detailedPosts = postsPage.stream().map(post -> detailedPostMapper.apply(authentication, post)).collect(Collectors.toList());
        return new PageImpl<>(detailedPosts, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("random/detailed/page")
    public Page<DetailedPostDTO> getDetailedPostsRandomly(Authentication authentication,
                                                               @RequestParam Integer size,
                                                               @RequestParam Integer page) {
        Page<Post> postsPage = service.getPostsPageRandomly(authentication, size, page);
        List<DetailedPostDTO> detailedPosts = postsPage.stream().map(post -> detailedPostMapper.apply(authentication, post)).collect(Collectors.toList());
        return new PageImpl<>(detailedPosts, postsPage.getPageable(), postsPage.getTotalElements());
    }

    @GetMapping("/{userId}/count")
    public Integer getPostsCountByUserId(@PathVariable Long userId) {
        return service.getPostsCountByUserId(userId);
    }

    @GetMapping("/all")
    public List<PostDTO> all() {
        return service.selectAllPosts().stream().map(postDTOMapper).collect(Collectors.toList());
    }
}
