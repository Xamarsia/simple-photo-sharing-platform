package com.xamarsia.simplephotosharingplatform.user.following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    Integer countAllByFollowingId(Long userId);

    Integer countAllByFollowerId(Long userId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Modifying
    @Transactional
    void deleteByFollowingIdAndFollowerId(Long followerId, Long followingId);
}
