package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    Page<Post> findPostsByUserUsername(String username, Pageable pageable);

    Integer countAllByUserId(Long userId);

    Page<Post> findPostsByUserIsIn(Set<User> followings, Pageable pageable);

    @Query(value = "SELECT * FROM POST ORDER BY RANDOM()", countQuery = "SELECT count(*) FROM POST ORDER BY RANDOM()", nativeQuery = true)
    Page<Post> findPostsRandomly(Pageable pageable);
}
