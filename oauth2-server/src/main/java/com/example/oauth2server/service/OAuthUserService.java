package com.example.oauth2server.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.oauth2server.entity.OAuthUser;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.repository.OAuthUserRepository;

@Service
public class OAuthUserService {

    private final OAuthUserRepository oAuthUserRepository;

    public OAuthUserService(OAuthUserRepository oAuthUserRepository) {
        this.oAuthUserRepository = oAuthUserRepository;
    }

    public OAuthUser getActiveUser(String username) {
        OAuthUser user = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new OAuthException("access_denied", "User not found", HttpStatus.BAD_REQUEST));
        if (!user.isActive()) {
            throw new OAuthException("access_denied", "User is inactive", HttpStatus.BAD_REQUEST);
        }
        return user;
    }
}
