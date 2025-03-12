package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.AWSException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.AccessDeniedException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.requests.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.requests.post.UpdatePostRequest;
import com.xamarsia.simplephotosharingplatform.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.s3.S3Service;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final UserService userService;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public Post createPost(Authentication authentication, CreatePostRequest req) {
        User user = userService.getAuthenticatedUser(authentication);
        Post post = Post.builder()
                .description(req.description())
                .user(user)
                .build();
        post = savePost(post);
        uploadPostImage(post.getId(), req.image());
        return post;
    }

    public Post updatePostInfo(Authentication authentication, UpdatePostRequest req, Long postId) {
        Post post = getPostById(postId);
        boolean isUserPostOwner = isCurrentUserOwner(authentication, post);

        if (!isUserPostOwner) {
            throw new AccessDeniedException("[UpdatePost]: Only post owner can update the post.");
        }

        var description = req.description();
        if (description != null) {
            post.setDescription(description);
        }

        post.setUpdateDateTime(OffsetDateTime.now());
        return savePost(post);
    }

    public void updatePostImage(Authentication authentication, MultipartFile file, Long postId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("[UpdatePostImage]: File is empty. Unable to save empty file");
        }

        Post post = getPostById(postId);
        boolean isUserPostOwner = isCurrentUserOwner(authentication, post);
        if (!isUserPostOwner) {
            throw new AccessDeniedException("[UpdatePostImage]: Only post owner can update the post.");
        }

        uploadPostImage(postId, file);
        post.setUpdateDateTime(OffsetDateTime.now());
        savePost(post);
    }

    public void deletePostById(Authentication authentication, Long postId) {
        Post post = getPostById(postId);
        boolean isUserPostOwner = isCurrentUserOwner(authentication, post);
        if (!isUserPostOwner) {
            throw new AccessDeniedException("[DeletePostById]: Only post owner can delete the post");
        }
        s3Service.deleteObject(s3Buckets.getPostsImages(), postId.toString());
        repository.deleteById(postId);
    }

    public Post getPostById(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("[GetPostById]: Post not found with id " + postId));
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsPageByUsername(String username, Integer pageNumber, Integer pageSize) {
        Pageable sortedByCreatedDate = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "creationDateTime"));
        return repository.findPostsByUserUsername(username, sortedByCreatedDate);
    }

    @Transactional(readOnly = true)
    public Page<Post> getNewsFeedPage(Integer pageSize, Integer pageNumber) {
        Pageable sortedByCreatedDate = PageRequest.of(pageNumber, pageSize);
        return repository.findPostsRandomly(sortedByCreatedDate);
    }

    @Transactional(readOnly = true)
    public User getPostAuthorByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return user;
    }

    @Transactional(readOnly = true)
    public Integer getPostsCountByUserId(Long userId) {
        return repository.countAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public byte[] getPostImage(Long postId) {
        // TODO: Check if postImage is empty or null
        return s3Service.getObject(s3Buckets.getPostsImages(),
                postId.toString());
    }

    @Transactional(readOnly = true)
    private boolean isCurrentUserOwner(Authentication authentication, Post post) {
        User user = userService.getAuthenticatedUser(authentication);
        return post.getUser() == user;
    }

    private Post savePost(Post post) {
        try {
            return repository.save(post);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SavePost]: " + e.getMessage());
        }
    }

    private void uploadPostImage(Long postId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("[UploadPostImage]: File is empty. Unable to save empty file");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if (!(Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg "))) {
            throw new IllegalArgumentException(String.format("[UploadPostImage]: Wrong file extension '%s' found. "
                    + "Only .jpg and .jpeg files are allowed.", extension));
        }
        try {
            s3Service.putObject(s3Buckets.getPostsImages(),
                    postId.toString(),
                    file.getBytes());
        } catch (IOException e) {
            throw new AWSException("[UploadPostImage]: " + e.getMessage());
        }
    }
}
