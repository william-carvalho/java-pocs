package com.example.oauth2server.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class OAuthRandomTokenGenerator {

    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }
}
