package com.example.oauth2server.dto;

public class TokenResponse {

    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;

    public TokenResponse(String accessToken, String tokenType, long expiresIn, String refreshToken, String scope) {
        this.access_token = accessToken;
        this.token_type = tokenType;
        this.expires_in = expiresIn;
        this.refresh_token = refreshToken;
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getScope() {
        return scope;
    }
}
