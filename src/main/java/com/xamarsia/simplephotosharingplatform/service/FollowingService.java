package com.xamarsia.simplephotosharingplatform.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xamarsia.simplephotosharingplatform.entity.Following;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.entity.PK.FollowingPK;
import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ApplicationException;
import com.xamarsia.simplephotosharingplatform.repository.FollowingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FollowingService {
    private final FollowingRepository repository;

    public Following follow(User follower, User following) {
        FollowingPK followingPK = new FollowingPK(follower, following);
        boolean isUserFollowedBy = isUserFollowedBy(followingPK);

        if (isUserFollowedBy) {
            throw new IllegalArgumentException("[Follow]: Invalid parameter. User already followed.");
        }

        Following newFollowing = Following.builder()
                .id(followingPK)
                .build();

        return saveFollowing(newFollowing);
    }

    public void deleteFollowing(User follower, User following) {
        FollowingPK followingPK = new FollowingPK(follower, following);
        boolean isUserFollowedBy = isUserFollowedBy(followingPK);

        if (!isUserFollowedBy) {
            throw new IllegalArgumentException("[DeleteFollowing]: User is not followed");
        }

        deleteFollowingById(followingPK);
    }

    @Transactional(readOnly = true)
    public Integer getFollowingsCountByUserId(Long userId) {
        return repository.countAllByIdFollowerId(userId);
    }

    @Transactional(readOnly = true)
    public Integer getFollowersCountByUserId(Long userId) {
        return repository.countAllByIdFollowingId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isUserFollowedBy(FollowingPK followingPK) {
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
