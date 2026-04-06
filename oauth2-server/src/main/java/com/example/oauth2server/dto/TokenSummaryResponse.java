package com.example.oauth2server.dto;

import java.time.LocalDateTime;

import com.example.oauth2server.entity.AccessToken;

public class TokenSummaryResponse {

    private Long id;
    private String clientId;
    private String username;
    private String scope;
    private String tokenType;
    private LocalDateTime expiresAt;
    private boolean revoked;

    public static TokenSummaryResponse from(AccessToken token) {
        TokenSummaryResponse response = new TokenSummaryResponse();
        response.id = token.getId();
        response.clientId = token.getClientId();
        response.username = token.getUsername();
        response.scope = token.getScope();
        response.tokenType = token.getTokenType();
        response.expiresAt = token.getExpiresAt();
        response.revoked = token.isRevoked();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }
}
