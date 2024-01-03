package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.dto.post.CreatePostRequest;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository repository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.repository = postRepository;
        this.userService = userService;
    }

    public Post savePost(Authentication authentication, CreatePostRequest req) {
        User user = userService.getAuthenticatedUser(authentication);
        Post post = Post.builder()
                .createdDate(LocalDateTime.now())
                .description(req.description())
                .imageUrl(req.imageUrl())
                .user(user)
                .build();
        return repository.save(post);
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

    public List<Post> selectAllPosts() {
        Page<Post> page = repository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    public Optional<Post> selectPostById(Long postId) {
        return repository.findById(postId);
    }

    public void deletePostById(Long postId) {
        repository.deleteById(postId);
    }
}
