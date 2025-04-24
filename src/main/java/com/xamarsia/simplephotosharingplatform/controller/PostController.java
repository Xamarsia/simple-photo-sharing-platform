package com.xamarsia.simplephotosharingplatform.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.dto.mapper.post.DetailedPostDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.mapper.post.PostDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.mapper.post.PostPreviewDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.post.DetailedPostDTO;
import com.xamarsia.simplephotosharingplatform.dto.post.PostDTO;
import com.xamarsia.simplephotosharingplatform.dto.post.PostPreviewDTO;
import com.xamarsia.simplephotosharingplatform.entity.Post;
import com.xamarsia.simplephotosharingplatform.request.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.request.post.UpdatePostRequest;
import com.xamarsia.simplephotosharingplatform.service.PostService;

import lombok.RequiredArgsConstructor;

/**
 * @brief Controller for handling post-related operations.
 * 
 *        This controller manages the endpoints related to posts,
 *        including creating, retrieving, updating, and deleting posts.
 */
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;
    private final PostDTOMapper postDTOMapper;
    private final PostPreviewDTOMapper postPreviewMapper;
    private final DetailedPostDTOMapper detailedPostMapper;

    /**
     * @brief Retrieves detailed information about a post by its ID.
     *
     *        Handles GET requests to the endpoint: `/post/{postId}/detailed`
     *
     * @param authentication The {@link Authentication} object.
     * @param postId         The ID of the post.
     * @return A {@link ResponseEntity} containing a {@link DetailedPostDTO} of the
     *         requested post.
     */
    @GetMapping("/{postId}/detailed")
    public ResponseEntity<DetailedPostDTO> getDetailedPostById(Authentication authentication,
            @PathVariable Long postId) {
        Post post = service.getPostById(postId);
        DetailedPostDTO postDTO = detailedPostMapper.apply(authentication, post);
        return ResponseEntity.ok().body(postDTO);
    }

    /**
     * @brief Retrieve a post by it's ID.
     * 
     *        Handles GET requests to the endpoint: `/post/{postId}`
     * 
     * @param postId The ID of the post.
     * @return A {@link ResponseEntity} containing a {@link PostDTO} of the
     *         requested post.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        Post post = service.getPostById(postId);
        PostDTO postDTO = postDTOMapper.apply(post);

        return ResponseEntity.ok().body(postDTO);
    }

    /**
     * @brief Retrieves the image associated with a post by its ID.
     * 
     *        Handles GET requests to the endpoint: `/post/{postId}/image`
     * 
     * @param postId The ID of the post.
     * @return A byte array in JPEG format representing the image.
     */
    @GetMapping(value = "/{postId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPostImage(@PathVariable Long postId) {
        return service.getPostImage(postId);
    }

    /**
     * @brief Creates a new post.
     * 
     *        Handles POST requests to the endpoint: `/post/create`
     * 
     * @param authentication The {@link Authentication} object containing current
     *                       user details.
     * @param newPost        The {@link CreatePostRequest} object containing the
     *                       data for the new post.
     * @return A {@link ResponseEntity} containing a {@link PostDTO} of the created
     *         post.
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(Authentication authentication,
            @ModelAttribute CreatePostRequest newPost) {
        Post savedPost = service.createPost(authentication, newPost);
        PostDTO postDTO = postDTOMapper.apply(savedPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    /**
     * @brief Update the image of an existing post.
     * 
     *        Handles PUT requests to the endpoint: `/post/{postId}/updateImage`
     * 
     * @param authentication The {@link Authentication} object containing current
     *                       user details.
     * @param file           The image file to be uploaded in JPEG format.
     * @param postId         The ID of the post for which the image is being
     *                       updated.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @PutMapping(value = "/{postId}/updateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmptyJson> updatePostImage(Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @PathVariable Long postId) {
        service.updatePostImage(authentication, file, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    /**
     * @brief Update the information of an existing post by its ID.
     * 
     *        Handles PUT requests to the endpoint: `/post/{postId}/updatePostInfo`
     * 
     * @param authentication The {@link Authentication} object containing current
     *                       user details.
     * @param newPost        The request object containing the new post information.
     * @param postId         The ID of the post to be updated.
     * @return A {@link ResponseEntity} containing the updated {@link PostDTO}.
     */
    @PutMapping("/{postId}/updatePostInfo")
    public ResponseEntity<PostDTO> updatePostInfo(Authentication authentication,
            @RequestBody UpdatePostRequest newPost,
            @PathVariable Long postId) {
        Post savedPost = service.updatePostInfo(authentication, newPost, postId);
        PostDTO postDTO = postDTOMapper.apply(savedPost);
        return ResponseEntity.status(HttpStatus.OK).body(postDTO);
    }

    /**
     * @brief Retrieve a paginated preview of posts for specific user by username.
     *        Posts are sorted by creation date and time from newest to oldest.
     * 
     *        Handles GET requests to the endpoint: `/post/preview/{username}`
     * 
     * @param username The username of the user whose posts are being retrieved.
     * @param size     The number of posts to retrieve per page.
     * @param page     The page number for pagination.
     * @return A {@link Page} containing a list of {@link PostPreviewDTO} for the
     *         requested user.
     */
    @GetMapping("/preview/{username}")
    public Page<PostPreviewDTO> getPostsPreviewPageByUsername(@PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getPostsPageByUsername(username, page, size);
        List<PostPreviewDTO> postsPreview = postsPage.stream().map(postPreviewMapper).collect(Collectors.toList());
        return new PageImpl<>(postsPreview, postsPage.getPageable(), postsPage.getTotalElements());
    }

    /**
     * @brief Retrieve a paginated news feed of posts.
     * 
     *        Handles GET requests to the endpoint: `/post/newsFeed`
     * 
     * @param authentication The {@link Authentication} object containing current
     *                       user details.
     * @param size           The number of posts to retrieve per page.
     * @param page           The page number for pagination.
     * @return A {@link Page} containing a list of {@link DetailedPostDTO} for the
     *         authenticated user's news feed.
     */
    @GetMapping("/newsFeed")
    public Page<DetailedPostDTO> getNewsFeedPage(Authentication authentication,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<Post> postsPage = service.getNewsFeedPage(size, page);
        List<DetailedPostDTO> detailedPosts = postsPage.stream()
                .map(post -> detailedPostMapper.apply(authentication, post)).collect(Collectors.toList());
        return new PageImpl<>(detailedPosts, postsPage.getPageable(), postsPage.getTotalElements());
    }

    /**
     * @brief Delete a specific post identified by its ID.
     * 
     *        Handles DELETE requests to the endpoint: `/post/{postId}`
     * 
     * @param authentication The {@link Authentication} object containing current
     *                       user details.
     * @param postId         The ID of the post to be deleted.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<EmptyJson> deletePost(Authentication authentication,
            @PathVariable Long postId) {
        service.deletePostById(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }
}
