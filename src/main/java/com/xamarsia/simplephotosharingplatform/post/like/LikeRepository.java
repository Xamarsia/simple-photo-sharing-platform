package com.xamarsia.simplephotosharingplatform.post.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikePK> {
    Integer countAllByPostId(Long userId);
}
