package com.xamarsia.simplephotosharingplatform.user.following;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xamarsia.simplephotosharingplatform.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FollowingService {
    private final FollowingRepository repository;

    public Following follow(User follower, User following) {
        boolean isUserInFollowing = isUserInFollowing(follower.getId(), following.getId());

        if (isUserInFollowing) {
            throw new IllegalArgumentException(String.format("[Follow]: Invalid parameter. User already followed."));
        }

        Following newfollowing = Following.builder()
                .follower(follower)
                .following(following)
                .build();

        return repository.save(newfollowing);
    }

    public void ufollow(Long followingId, Long followerId) {
        repository.deleteByFollowingIdAndFollowerId(followerId, followingId);
        return;
    }

    @Transactional(readOnly = true)
    public Integer getFollowingsCountByUserId(Long userId) {
        return repository.countAllByFollowerId(userId);
    }

    @Transactional(readOnly = true)
    public Integer getFollowersCountByUserId(Long userId) {
        return repository.countAllByFollowingId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isUserInFollowing(Long followingId, Long followerId) {
        return repository.existsByFollowerIdAndFollowingId(followingId, followerId);
    }
}
