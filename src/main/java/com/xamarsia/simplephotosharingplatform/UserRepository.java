package com.xamarsia.simplephotosharingplatform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Set<User> findAllByIdIn(Set<Long> idSet);
    boolean existsUserByEmail (String email);
    boolean existsUserById (Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User c SET c.profileImageId = ?1 WHERE c.id = ?2")
    Long updateProfileImageId(String profileImageId, Long customerId);
}
