package com.xamarsia.simplephotosharingplatform.email;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Boolean existsByEmail(String email);

    Optional<EmailVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
