package com.example.oauth2server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oauth2server.entity.OAuthUser;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    Optional<OAuthUser> findByUsername(String username);
}
