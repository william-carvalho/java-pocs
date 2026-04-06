package com.example.oauth2server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.oauth2server.config.OAuthProperties;
import com.example.oauth2server.dto.TokenResponse;
import com.example.oauth2server.dto.TokenSummaryResponse;
import com.example.oauth2server.entity.AccessToken;
import com.example.oauth2server.entity.AuthorizationCode;
import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.entity.RefreshToken;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.repository.AccessTokenRepository;
import com.example.oauth2server.repository.RefreshTokenRepository;
import com.example.oauth2server.security.OAuthRandomTokenGenerator;

@Service
public class TokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthRandomTokenGenerator tokenGenerator;
    private final OAuthProperties properties;

    public TokenService(AccessTokenRepository accessTokenRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        OAuthRandomTokenGenerator tokenGenerator,
                        OAuthProperties properties) {
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.properties = properties;
    }

    public TokenResponse issueForAuthorizationCode(OAuthClient client, AuthorizationCode code) {
        AccessToken accessToken = buildAccessToken(client.getClientId(), code.getUsername(), code.getScope());
        RefreshToken refreshToken = buildRefreshToken(client.getClientId(), code.getUsername());

        accessTokenRepository.save(accessToken);
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken.getToken(), accessToken.getTokenType(),
                properties.getAccessTokenExpirationSeconds(), refreshToken.getToken(), accessToken.getScope());
    }

    public TokenResponse issueForClientCredentials(OAuthClient client, String scope) {
        AccessToken accessToken = buildAccessToken(client.getClientId(), null, scope);
        accessTokenRepository.save(accessToken);
        return new TokenResponse(accessToken.getToken(), accessToken.getTokenType(),
                properties.getAccessTokenExpirationSeconds(), null, accessToken.getScope());
    }

    public TokenResponse issueFromRefreshToken(OAuthClient client, String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new OAuthException("invalid_grant", "Refresh token is invalid or expired",
                        HttpStatus.BAD_REQUEST));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OAuthException("invalid_grant", "Refresh token is invalid or expired", HttpStatus.BAD_REQUEST);
        }
        if (!refreshToken.getClientId().equals(client.getClientId())) {
            throw new OAuthException("invalid_grant", "Refresh token does not belong to this client",
                    HttpStatus.BAD_REQUEST);
        }

        AccessToken accessToken = buildAccessToken(client.getClientId(), refreshToken.getUsername(), client.getScopes());
        accessTokenRepository.save(accessToken);
        return new TokenResponse(accessToken.getToken(), accessToken.getTokenType(),
                properties.getAccessTokenExpirationSeconds(), refreshToken.getToken(), accessToken.getScope());
    }

    public List<TokenSummaryResponse> listAll() {
        return accessTokenRepository.findAll()
                .stream()
                .map(TokenSummaryResponse::from)
                .collect(Collectors.toList());
    }

    public void revoke(String tokenValue) {
        AccessToken accessToken = accessTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new OAuthException("invalid_request", "Token not found", HttpStatus.BAD_REQUEST));
        accessToken.setRevoked(true);
        accessTokenRepository.save(accessToken);
    }

    private AccessToken buildAccessToken(String clientId, String username, String scope) {
        AccessToken token = new AccessToken();
        token.setToken(tokenGenerator.generateToken());
        token.setClientId(clientId);
        token.setUsername(username);
        token.setScope(scope);
        token.setTokenType("bearer");
        token.setExpiresAt(LocalDateTime.now().plusSeconds(properties.getAccessTokenExpirationSeconds()));
        token.setRevoked(false);
        return token;
    }

    private RefreshToken buildRefreshToken(String clientId, String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenGenerator.generateToken());
        refreshToken.setClientId(clientId);
        refreshToken.setUsername(username);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(properties.getRefreshTokenExpirationSeconds()));
        refreshToken.setRevoked(false);
        return refreshToken;
    }
}
