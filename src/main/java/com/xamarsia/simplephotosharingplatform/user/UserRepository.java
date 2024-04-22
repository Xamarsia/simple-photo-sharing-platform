package com.xamarsia.simplephotosharingplatform.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String email);

    Page<User> findUsersByFollowers(User follower, Pageable pageable);

    Page<User> findUsersByFollowings(User followings, Pageable pageable);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserById(Long id);
}
