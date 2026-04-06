package com.example.oauth2server.dto;

public class ConfigResponse {

    private final long accessTokenExpirationSeconds;
    private final long authorizationCodeExpirationSeconds;
    private final long refreshTokenExpirationSeconds;
    private final String[] supportedGrantTypes;

    public ConfigResponse(long accessTokenExpirationSeconds,
                          long authorizationCodeExpirationSeconds,
                          long refreshTokenExpirationSeconds,
                          String[] supportedGrantTypes) {
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.authorizationCodeExpirationSeconds = authorizationCodeExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
        this.supportedGrantTypes = supportedGrantTypes;
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public long getAuthorizationCodeExpirationSeconds() {
        return authorizationCodeExpirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    public String[] getSupportedGrantTypes() {
        return supportedGrantTypes;
    }
}
