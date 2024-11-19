package com.xamarsia.simplephotosharingplatform.user.following;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, FollowingPK> {

    Integer countAllByIdFollowingId(Long userId);

    Integer countAllByIdFollowerId(Long userId);
}
