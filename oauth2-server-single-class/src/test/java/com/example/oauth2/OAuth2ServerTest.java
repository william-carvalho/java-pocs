package com.example.oauth2;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth2ServerTest {
    @Test
    void authorizationEndpointIssuesCodeAndPreservesState() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.AuthorizationResponse response = server.authorize(
                "code",
                "client-app",
                "http://localhost:8081/callback",
                "read",
                "xyz",
                "william");

        assertNotNull(response.getCode());
        assertEquals("xyz", response.getState());
        assertTrue(response.getRedirectLocation().startsWith("http://localhost:8081/callback?code="));
        assertTrue(response.getRedirectLocation().contains("&state=xyz"));
    }

    @Test
    void authorizationCodeGrantIssuesAccessAndRefreshToken() {
        OAuth2Server server = OAuth2Server.withDefaultData();
        OAuth2Server.AuthorizationResponse authorization = server.authorize(
                "code", "client-app", "http://localhost:8081/callback", "read write", "abc", "william");

        OAuth2Server.TokenResponse token = server.token(OAuth2Server.tokenRequest("authorization_code", "client-app", "secret123")
                .code(authorization.getCode())
                .redirectUri("http://localhost:8081/callback"));

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
        assertEquals("Bearer", token.getTokenType());
        assertEquals(3600L, token.getExpiresIn());
        assertEquals("read write", token.getScope());
        assertTrue(server.isTokenActive(token.getAccessToken()));
    }

    @Test
    void authorizationCodeCannotBeReused() {
        OAuth2Server server = OAuth2Server.withDefaultData();
        OAuth2Server.AuthorizationResponse authorization = server.authorize(
                "code", "client-app", "http://localhost:8081/callback", "read", null, "william");
        OAuth2Server.TokenRequest request = OAuth2Server.tokenRequest("authorization_code", "client-app", "secret123")
                .code(authorization.getCode())
                .redirectUri("http://localhost:8081/callback");
        server.token(request);

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () -> server.token(request));

        assertEquals("invalid_grant", error.getError());
    }

    @Test
    void expiredAuthorizationCodeIsRejected() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 17, 10, 0));
        OAuth2Server server = new OAuth2Server(clock, 10, 3600, 86400);
        seedAuthorizationClient(server);
        OAuth2Server.AuthorizationResponse authorization = server.authorize(
                "code", "client-app", "http://localhost/callback", "read", null, "william");
        clock.advanceSeconds(10);

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () ->
                server.token(OAuth2Server.tokenRequest("authorization_code", "client-app", "secret")
                        .code(authorization.getCode())
                        .redirectUri("http://localhost/callback")));

        assertEquals("invalid_grant", error.getError());
    }

    @Test
    void clientCredentialsGrantIssuesAccessTokenWithoutRefreshToken() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.TokenResponse token = server.token(OAuth2Server.tokenRequest("client_credentials", "service-client", "service-secret")
                .scope("read"));

        assertNotNull(token.getAccessToken());
        assertNull(token.getRefreshToken());
        assertEquals("read", token.getScope());
        assertTrue(server.isTokenActive(token.getAccessToken()));
    }

    @Test
    void passwordGrantIssuesTokensForValidResourceOwnerCredentials() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.TokenResponse token = server.token(OAuth2Server.tokenRequest("password", "client-app", "secret123")
                .username("maria")
                .password("123456")
                .scope("read"));

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
        assertEquals("read", token.getScope());
    }

    @Test
    void refreshTokenGrantIssuesNewAccessToken() {
        OAuth2Server server = OAuth2Server.withDefaultData();
        OAuth2Server.AuthorizationResponse authorization = server.authorize(
                "code", "client-app", "http://localhost:8081/callback", "read", null, "william");
        OAuth2Server.TokenResponse original = server.token(OAuth2Server.tokenRequest("authorization_code", "client-app", "secret123")
                .code(authorization.getCode())
                .redirectUri("http://localhost:8081/callback"));

        OAuth2Server.TokenResponse refreshed = server.token(OAuth2Server.tokenRequest("refresh_token", "client-app", "secret123")
                .refreshToken(original.getRefreshToken()));

        assertNotNull(refreshed.getAccessToken());
        assertEquals(original.getRefreshToken(), refreshed.getRefreshToken());
        assertTrue(server.isTokenActive(refreshed.getAccessToken()));
    }

    @Test
    void invalidClientSecretReturnsOAuthError() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () ->
                server.token(OAuth2Server.tokenRequest("client_credentials", "service-client", "bad")));

        assertEquals("invalid_client", error.getError());
        assertEquals("Client authentication failed", error.getErrorDescription());
    }

    @Test
    void invalidRedirectUriIsRejected() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () ->
                server.authorize("code", "client-app", "http://evil/callback", "read", null, "william"));

        assertEquals("invalid_request", error.getError());
    }

    @Test
    void invalidScopeIsRejected() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () ->
                server.token(OAuth2Server.tokenRequest("client_credentials", "service-client", "service-secret")
                        .scope("write")));

        assertEquals("invalid_scope", error.getError());
    }

    @Test
    void unauthorizedGrantTypeIsRejected() {
        OAuth2Server server = OAuth2Server.withDefaultData();

        OAuth2Server.OAuthException error = assertThrows(OAuth2Server.OAuthException.class, () ->
                server.token(OAuth2Server.tokenRequest("password", "service-client", "service-secret")
                        .username("william")
                        .password("123456")));

        assertEquals("unauthorized_client", error.getError());
    }

    @Test
    void revokeMakesAccessTokenInactive() {
        OAuth2Server server = OAuth2Server.withDefaultData();
        OAuth2Server.TokenResponse token = server.token(OAuth2Server.tokenRequest("client_credentials", "service-client", "service-secret")
                .scope("read"));

        assertTrue(server.revoke(token.getAccessToken()));

        assertFalse(server.isTokenActive(token.getAccessToken()));
        assertFalse(server.revoke("missing"));
    }

    @Test
    void expiredAccessTokenIsInactive() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 17, 10, 0));
        OAuth2Server server = new OAuth2Server(clock, 300, 5, 86400);
        server.registerClient("service", "secret", "http://localhost/callback", OAuth2Server.set("read"), OAuth2Server.set("client_credentials"), true);
        OAuth2Server.TokenResponse token = server.token(OAuth2Server.tokenRequest("client_credentials", "service", "secret").scope("read"));
        clock.advanceSeconds(5);

        assertFalse(server.isTokenActive(token.getAccessToken()));
    }

    @Test
    void listTokensAndConfigExposeDebugInformation() {
        OAuth2Server server = OAuth2Server.withDefaultData();
        server.token(OAuth2Server.tokenRequest("client_credentials", "service-client", "service-secret").scope("read"));

        assertEquals(1, server.listTokens().size());
        assertEquals(300L, server.config().getAuthorizationCodeTtlSeconds());
        assertEquals(3600L, server.config().getAccessTokenTtlSeconds());
        assertEquals(86400L, server.config().getRefreshTokenTtlSeconds());
    }

    private static void seedAuthorizationClient(OAuth2Server server) {
        server.registerUser("william", "123456");
        server.registerClient(
                "client-app",
                "secret",
                "http://localhost/callback",
                OAuth2Server.set("read"),
                OAuth2Server.set("authorization_code", "refresh_token"),
                true);
    }

    private static final class MutableClock implements OAuth2Server.Clock {
        private LocalDateTime now;

        private MutableClock(LocalDateTime now) {
            this.now = now;
        }

        public LocalDateTime now() {
            return now;
        }

        private void advanceSeconds(long seconds) {
            now = now.plusSeconds(seconds);
        }
    }
}
