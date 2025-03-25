package com.xamarsia.simplephotosharingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xamarsia.simplephotosharingplatform.entity.Like;
import com.xamarsia.simplephotosharingplatform.entity.PK.LikePK;

public interface LikeRepository extends JpaRepository<Like, LikePK> {
    Integer countAllByIdPostId(Long postId);
}
