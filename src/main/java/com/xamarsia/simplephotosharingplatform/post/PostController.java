package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.dto.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.dto.post.PostUpdateRequest;
import com.xamarsia.simplephotosharingplatform.post.preview.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.post.preview.PostPreviewDTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        Post post = service.getPostById(postId);
        PostDTO postDTO = postDTOMapper.apply(post);

        return ResponseEntity.ok().body(postDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(Authentication authentication,
            @Valid @RequestBody CreatePostRequest newPost)
    {
        Post savedPost = service.savePost(authentication, newPost);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @PutMapping("/{postId}/update")
    public ResponseEntity<?> updatePost(Authentication authentication,
                                        @Valid @RequestBody PostUpdateRequest newPost,
                                        @PathVariable Long postId) {
        Post savedPost = service.updatePost(authentication, newPost, postId);
        PostDTO postDTO = postDTOMapper.apply(savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @GetMapping("/{userId}/all")
    public List<PostDTO> getPostsByUserId(@PathVariable Long userId) {
        return service.findPostsByUserId(userId).stream().map(postDTOMapper).collect(Collectors.toList());
    }

    @GetMapping("preview/{userId}/all")
    public List<PostPreviewDTO> getPostsPreviewByUserId(@PathVariable Long userId) {
        return service.findPostsByUserId(userId).stream().map(postPreviewMapper).collect(Collectors.toList());
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
