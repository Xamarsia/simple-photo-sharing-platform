package com.xamarsia.simplephotosharingplatform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xamarsia.simplephotosharingplatform.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findPostsByUserUsername(String username, Pageable pageable);

    Integer countAllByUserId(Long userId);

    @Query(value = "SELECT * FROM POST ORDER BY RANDOM()", countQuery = "SELECT count(*) FROM POST ORDER BY RANDOM()", nativeQuery = true)
    Page<Post> findPostsRandomly(Pageable pageable);
}
