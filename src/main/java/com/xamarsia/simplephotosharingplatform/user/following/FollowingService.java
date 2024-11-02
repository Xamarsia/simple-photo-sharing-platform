package com.xamarsia.simplephotosharingplatform.user.following;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FollowingService {
    private final FollowingRepository repository;

    public Following follow(User follower, User following) {

        FollowingPK followingPK = new FollowingPK(follower.getId(), following.getId());
        boolean isUserInFollowing = isUserInFollowing(followingPK);

        if (isUserInFollowing) {
            throw new IllegalArgumentException("[Follow]: Invalid parameter. User already followed.");
        }

        Following newfollowing = Following.builder()
                .id(followingPK)
                .follower(follower)
                .following(following)
                .build();

        return saveFollowing(newfollowing);
    }

    public void ufollow(Long followingId, Long followerId) {
        FollowingPK followingPK = new FollowingPK(followingId, followerId);
        boolean isUserInFollowing = isUserInFollowing(followingPK);

        if (!isUserInFollowing) {
            throw new IllegalArgumentException("[Ufollow]: User is not followed");
        }

        deleteFollowingById(followingPK);
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
        FollowingPK followingPK = new FollowingPK(followingId, followerId);
        return isUserInFollowing(followingPK);
    }

    @Transactional(readOnly = true)
    private boolean isUserInFollowing(FollowingPK followingPK) {
        return repository.existsById(followingPK);
    }

    private Following saveFollowing(Following following) {
        try {
            return repository.save(following);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveFollowing]: " + e.getMessage());
        }
    }

    private void deleteFollowingById(FollowingPK followingPK) {
        try {
            repository.deleteById(followingPK);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[DeleteFollowing]: " + e.getMessage());
        }
    }
}
