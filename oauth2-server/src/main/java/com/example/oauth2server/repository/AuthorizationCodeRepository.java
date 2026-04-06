package com.example.oauth2server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oauth2server.entity.AuthorizationCode;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Long> {

    Optional<AuthorizationCode> findByCode(String code);
}
