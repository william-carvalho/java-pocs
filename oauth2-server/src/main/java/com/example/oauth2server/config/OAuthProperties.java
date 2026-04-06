package com.example.oauth2server.config;

import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.oauth")
public class OAuthProperties {

    @Min(1)
    private long accessTokenExpirationSeconds = 3600L;

    @Min(1)
    private long authorizationCodeExpirationSeconds = 300L;

    @Min(1)
    private long refreshTokenExpirationSeconds = 86400L;

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public void setAccessTokenExpirationSeconds(long accessTokenExpirationSeconds) {
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public long getAuthorizationCodeExpirationSeconds() {
        return authorizationCodeExpirationSeconds;
    }

    public void setAuthorizationCodeExpirationSeconds(long authorizationCodeExpirationSeconds) {
        this.authorizationCodeExpirationSeconds = authorizationCodeExpirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    public void setRefreshTokenExpirationSeconds(long refreshTokenExpirationSeconds) {
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }
}
