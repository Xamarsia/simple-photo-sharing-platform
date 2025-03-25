package com.xamarsia.simplephotosharingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xamarsia.simplephotosharingplatform.entity.Auth;

public interface AuthRepository extends JpaRepository<Auth, String> {

}
