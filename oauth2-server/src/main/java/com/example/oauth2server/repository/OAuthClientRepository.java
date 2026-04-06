package com.example.oauth2server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oauth2server.entity.OAuthClient;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {

    Optional<OAuthClient> findByClientId(String clientId);
}
