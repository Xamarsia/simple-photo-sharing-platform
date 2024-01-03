package com.xamarsia.simplephotosharingplatform.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    Integer countPostsByUserId(Long userId);

    Integer countAllByUserId(Long userId);

    List<Post> findAllByUserUsername(String username);
}