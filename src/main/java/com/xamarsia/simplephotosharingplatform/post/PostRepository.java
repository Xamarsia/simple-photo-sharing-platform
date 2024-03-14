package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    Page<Post> findPostsByUserId(Long userId, Pageable pageable);

    Integer countPostsByUserId(Long userId);

    boolean existsPostById(Long postId);

    Integer countAllByUserId(Long userId);

    List<Post> findAllByUserUsername(String username);

    Page<Post> findPostsByUserIsIn(Set<User> followings, Pageable pageable);
}