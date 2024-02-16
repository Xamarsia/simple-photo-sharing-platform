package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.dto.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.dto.post.UpdatePostRequest;
import com.xamarsia.simplephotosharingplatform.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.s3.S3Service;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository repository;
    private final UserService userService;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public PostService(PostRepository postRepository, UserService userService, S3Service s3Service, S3Buckets s3Buckets) {
        this.repository = postRepository;
        this.userService = userService;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public void uploadPostImage(Long postId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("[UploadPostImage]: File is empty. Unable to save empty file");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if (!(Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg "))) {
            throw new RuntimeException("[UploadPostImage]: Wrong file extension found: " + extension
                    + ". Only .jpg and .jpeg files are allowed.");
        }
        try {
            s3Service.putObject(s3Buckets.getPostsImages(),
                    postId.toString(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("[UploadPostImage]: " + e.getMessage());
        }
    }

    public Post savePost(Authentication authentication, CreatePostRequest req) {
        User user = userService.getAuthenticatedUser(authentication);
        Post post = Post.builder()
                .createdDate(LocalDateTime.now())
                .description(req.getDescription())
                .user(user)
                .build();
        post = repository.save(post);
        uploadPostImage(post.getId(), req.getImage());
        return post;
    }

    public Post updatePost(Authentication authentication, UpdatePostRequest req, Long postId) {
        if (req.isEmpty()) {
            throw new RuntimeException("[UpdatePost]: Invalid argument, UpdatePostRequest is empty");
        }

        Post post = selectPostById(postId)
                .orElseThrow(() -> new RuntimeException("[UpdatePost]: Post not found with id " + postId));

        boolean isUserPostOwner = isAuthenticatedUserIsPostOwner(authentication, post);
        if (!isUserPostOwner) {
            throw new RuntimeException("[UpdatePost]: Only post owner can update the post");
        }

        var description = req.getDescription();
        if (description != null) {
            post.setDescription(description);
        }

        var image = req.getImage();
        if (image != null) {
            uploadPostImage(post.getId(), req.getImage());
        }

        post.setCreatedDate(LocalDateTime.now());
        return repository.save(post);
    }

    public boolean isPostWithIdExist(Long postId) {
        return repository.existsPostById(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Integer getPostsCountByUserId(Long userId) {
        return repository.countAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return selectPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));
    }

    @Transactional(readOnly = true)
    public byte[] getPostImage(Long postId) {
        //TODO: Check if postImage is empty or null
        return s3Service.getObject(s3Buckets.getPostsImages(),
                postId.toString());
    }

    public List<Post> selectAllPosts() {
        Page<Post> page = repository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    public Optional<Post> selectPostById(Long postId) {
        return repository.findById(postId);
    }

    private boolean isAuthenticatedUserIsPostOwner(Authentication authentication, Long postId) {
        User user = userService.getAuthenticatedUser(authentication);
        Post post = selectPostById(postId).orElseThrow(() -> new RuntimeException("Post not found with id " + postId));
        return post.getUser() == user;
    }

    private boolean isAuthenticatedUserIsPostOwner(Authentication authentication, Post post) {
        User user = userService.getAuthenticatedUser(authentication);
        return post.getUser() == user;
    }

    public void deletePostById(Authentication authentication, Long postId) {
        boolean isUserPostOwner = isAuthenticatedUserIsPostOwner(authentication, postId);
        if (!isUserPostOwner) {
            throw new RuntimeException("Delete post: Only post owner can delete the post");
        }

        if (!isPostWithIdExist(postId)) {
            throw new RuntimeException("Delete post by Id: Post not found with id " + postId);
        }
        repository.deleteById(postId);
    }
}
