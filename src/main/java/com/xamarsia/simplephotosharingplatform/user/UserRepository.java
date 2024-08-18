package com.xamarsia.simplephotosharingplatform.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Page<User> findUsersByFollowers(User follower, Pageable pageable);

    Page<User> findUsersByFollowings(User followings, Pageable pageable);

    boolean existsUserByUsername(String username);

    boolean existsUserById(Long id);

    @Query(value = "Select * from _user u WHERE (u.username LIKE CONCAT('%',:substring,'%') OR u.full_name LIKE CONCAT('%',:substring,'%'))", countQuery = "SELECT count(*) FROM _user", nativeQuery = true)
    Page<User> searchUserBySubstring(@Param("substring") String substring, Pageable pageable);
}
