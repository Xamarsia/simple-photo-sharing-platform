package com.xamarsia.simplephotosharingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xamarsia.simplephotosharingplatform.entity.Following;
import com.xamarsia.simplephotosharingplatform.entity.PK.FollowingPK;

public interface FollowingRepository extends JpaRepository<Following, FollowingPK> {

    Integer countAllByIdFollowingId(Long userId);

    Integer countAllByIdFollowerId(Long userId);
}
