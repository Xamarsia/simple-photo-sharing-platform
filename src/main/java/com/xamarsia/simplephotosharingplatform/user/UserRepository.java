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

    @Query(value = "Select * from _user u WHERE (lower(u.username) LIKE lower(CONCAT('%',:substring,'%')) OR lower(u.full_name) LIKE lower(CONCAT('%',:substring,'%')))", countQuery = "SELECT count(*) FROM _user", nativeQuery = true)
    Page<User> searchUserBySubstring(@Param("substring") String substring, Pageable pageable); //searchUser

    @Query(value = "select u.* from _user u join following f on u.id = f.following_id WHERE f.follower_id = ?1", countQuery = "SELECT count(*) FROM following", nativeQuery = true)
    Page<User> findFollowingsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select u.* from _user u join following f on u.id = f.follower_id WHERE f.following_id = ?1", countQuery = "SELECT count(*) FROM following", nativeQuery = true)
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select u.* from _user u join _like l on u.id = l.user_id WHERE l.post_id = ?1", countQuery = "SELECT count(*) FROM _like", nativeQuery = true)
    Page<User> findPostLikersByPostId(@Param("postId") Long postId, Pageable pageable); // findUsersLikedPostPage
}
