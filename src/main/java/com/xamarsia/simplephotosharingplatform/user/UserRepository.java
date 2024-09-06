package com.xamarsia.simplephotosharingplatform.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserById(Long id);

    @Query(value = "Select * from _user u WHERE (u.username LIKE CONCAT('%',:substring,'%') OR u.full_name LIKE CONCAT('%',:substring,'%'))", countQuery = "SELECT count(*) FROM _user", nativeQuery = true)
    Page<User> searchUserBySubstring(@Param("substring") String substring, Pageable pageable);

    @Query(value = "select u.* from _user u join following f on u.id = f.following_id WHERE f.follower_id = ?1", countQuery = "SELECT count(*) FROM following", nativeQuery = true)
    Page<User> findFollowingsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select u.* from _user u join following f on u.id = f.follower_id WHERE f.following_id = ?1", countQuery = "SELECT count(*) FROM following", nativeQuery = true)
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);
}
