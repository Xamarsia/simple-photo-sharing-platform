package com.xamarsia.simplephotosharingplatform.security.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, String> {

}
