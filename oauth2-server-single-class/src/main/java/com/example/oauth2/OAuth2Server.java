package com.example.oauth2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class OAuth2Server {
    private final Map<String, OAuthClient> clients = new LinkedHashMap<String, OAuthClient>();
    private final Map<String, OAuthUser> users = new LinkedHashMap<String, OAuthUser>();
    private final Map<String, AuthorizationCode> authorizationCodes = new LinkedHashMap<String, AuthorizationCode>();
    private final Map<String, AccessToken> accessTokens = new LinkedHashMap<String, AccessToken>();
    private final Map<String, RefreshToken> refreshTokens = new LinkedHashMap<String, RefreshToken>();
    private final Clock clock;
    private final long authorizationCodeTtlSeconds;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public OAuth2Server() {
        this(new SystemClock(), 300L, 3600L, 86400L);
    }

    public OAuth2Server(Clock clock, long authorizationCodeTtlSeconds, long accessTokenTtlSeconds, long refreshTokenTtlSeconds) {
        if (clock == null) {
            throw new IllegalArgumentException("clock is required");
        }
        this.clock = clock;
        this.authorizationCodeTtlSeconds = authorizationCodeTtlSeconds;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public OAuthClient registerClient(String clientId,
                                      String clientSecret,
                                      String redirectUri,
                                      Set<String> scopes,
                                      Set<String> grantTypes,
                                      boolean confidential) {
        OAuthClient client = new OAuthClient(clientId, clientSecret, redirectUri, scopes, grantTypes, confidential);
        if (clients.containsKey(client.getClientId())) {
            throw new OAuthException("invalid_client", "Client already exists");
        }
        clients.put(client.getClientId(), client);
        return client;
    }

    public OAuthUser registerUser(String username, String password) {
        OAuthUser user = new OAuthUser(username, password);
        users.put(user.getUsername(), user);
        return user;
    }

    public AuthorizationResponse authorize(String responseType,
                                           String clientId,
                                           String redirectUri,
                                           String scope,
                                           String state,
                                           String username) {
        if (!"code".equals(responseType)) {
            throw new OAuthException("unsupported_response_type", "Only authorization code is supported");
        }
        OAuthClient client = requireClient(clientId);
        OAuthUser user = requireUser(username);
        validateRedirectUri(client, redirectUri);
        validateGrantType(client, "authorization_code");
        Set<String> requestedScopes = parseScopes(scope);
        validateScopes(client, requestedScopes);

        AuthorizationCode code = new AuthorizationCode(
                randomToken("code"),
                client.getClientId(),
                user.getUsername(),
                redirectUri,
                requestedScopes,
                clock.now(),
                clock.now().plusSeconds(authorizationCodeTtlSeconds));
        authorizationCodes.put(code.getCode(), code);
        return new AuthorizationResponse(code.getCode(), state, redirectUri + "?code=" + code.getCode() + appendState(state));
    }

    public TokenResponse token(TokenRequest request) {
        if (request == null) {
            throw new OAuthException("invalid_request", "Token request is required");
        }
        if ("authorization_code".equals(request.getGrantType())) {
            return tokenFromAuthorizationCode(request);
        }
        if ("client_credentials".equals(request.getGrantType())) {
            return tokenFromClientCredentials(request);
        }
        if ("password".equals(request.getGrantType())) {
            return tokenFromPassword(request);
        }
        if ("refresh_token".equals(request.getGrantType())) {
            return tokenFromRefreshToken(request);
        }
        throw new OAuthException("unsupported_grant_type", "Grant type is not supported");
    }

    public boolean revoke(String accessTokenValue) {
        AccessToken token = accessTokens.get(accessTokenValue);
        if (token == null) {
            return false;
        }
        token.revoke();
        return true;
    }

    public boolean isTokenActive(String accessTokenValue) {
        AccessToken token = accessTokens.get(accessTokenValue);
        return token != null && !token.isRevoked() && !token.isExpired(clock.now());
    }

    public List<AccessToken> listTokens() {
        return Collections.unmodifiableList(new ArrayList<AccessToken>(accessTokens.values()));
    }

    public OAuthConfig config() {
        return new OAuthConfig(authorizationCodeTtlSeconds, accessTokenTtlSeconds, refreshTokenTtlSeconds);
    }

    public static OAuth2Server withDefaultData() {
        OAuth2Server server = new OAuth2Server();
        server.registerUser("william", "123456");
        server.registerUser("maria", "123456");
        server.registerClient(
                "client-app",
                "secret123",
                "http://localhost:8081/callback",
                set("read", "write"),
                set("authorization_code", "refresh_token", "password"),
                true);
        server.registerClient(
                "service-client",
                "service-secret",
                "http://localhost/service-callback",
                set("read"),
                set("client_credentials"),
                true);
        return server;
    }

    public static TokenRequest tokenRequest(String grantType, String clientId, String clientSecret) {
        return new TokenRequest(grantType, clientId, clientSecret);
    }

    public static Set<String> set(String... values) {
        return new LinkedHashSet<String>(Arrays.asList(values));
    }

    private TokenResponse tokenFromAuthorizationCode(TokenRequest request) {
        OAuthClient client = authenticateClient(request.getClientId(), request.getClientSecret());
        validateGrantType(client, "authorization_code");
        AuthorizationCode code = authorizationCodes.get(request.getCode());
        if (code == null || code.isUsed() || code.isExpired(clock.now())) {
            throw new OAuthException("invalid_grant", "Authorization code is invalid or expired");
        }
        if (!client.getClientId().equals(code.getClientId())) {
            throw new OAuthException("invalid_grant", "Authorization code belongs to another client");
        }
        validateRedirectUri(client, request.getRedirectUri());
        if (!code.getRedirectUri().equals(request.getRedirectUri())) {
            throw new OAuthException("invalid_grant", "Redirect URI does not match authorization code");
        }
        code.markUsed();
        AccessToken accessToken = issueAccessToken(client.getClientId(), code.getUsername(), code.getScopes());
        RefreshToken refreshToken = issueRefreshToken(client.getClientId(), code.getUsername(), code.getScopes());
        return new TokenResponse(accessToken.getValue(), "Bearer", accessTokenTtlSeconds, refreshToken.getValue(), joinScopes(code.getScopes()));
    }

    private TokenResponse tokenFromClientCredentials(TokenRequest request) {
        OAuthClient client = authenticateClient(request.getClientId(), request.getClientSecret());
        validateGrantType(client, "client_credentials");
        Set<String> requestedScopes = parseScopes(request.getScope());
        validateScopes(client, requestedScopes);
        AccessToken accessToken = issueAccessToken(client.getClientId(), null, requestedScopes);
        return new TokenResponse(accessToken.getValue(), "Bearer", accessTokenTtlSeconds, null, joinScopes(requestedScopes));
    }

    private TokenResponse tokenFromPassword(TokenRequest request) {
        OAuthClient client = authenticateClient(request.getClientId(), request.getClientSecret());
        validateGrantType(client, "password");
        OAuthUser user = users.get(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new OAuthException("invalid_grant", "Invalid resource owner credentials");
        }
        Set<String> requestedScopes = parseScopes(request.getScope());
        validateScopes(client, requestedScopes);
        AccessToken accessToken = issueAccessToken(client.getClientId(), user.getUsername(), requestedScopes);
        RefreshToken refreshToken = issueRefreshToken(client.getClientId(), user.getUsername(), requestedScopes);
        return new TokenResponse(accessToken.getValue(), "Bearer", accessTokenTtlSeconds, refreshToken.getValue(), joinScopes(requestedScopes));
    }

    private TokenResponse tokenFromRefreshToken(TokenRequest request) {
        OAuthClient client = authenticateClient(request.getClientId(), request.getClientSecret());
        validateGrantType(client, "refresh_token");
        RefreshToken refreshToken = refreshTokens.get(request.getRefreshToken());
        if (refreshToken == null || refreshToken.isRevoked() || refreshToken.isExpired(clock.now())) {
            throw new OAuthException("invalid_grant", "Refresh token is invalid or expired");
        }
        if (!client.getClientId().equals(refreshToken.getClientId())) {
            throw new OAuthException("invalid_grant", "Refresh token belongs to another client");
        }
        AccessToken accessToken = issueAccessToken(client.getClientId(), refreshToken.getUsername(), refreshToken.getScopes());
        return new TokenResponse(accessToken.getValue(), "Bearer", accessTokenTtlSeconds, refreshToken.getValue(), joinScopes(refreshToken.getScopes()));
    }

    private AccessToken issueAccessToken(String clientId, String username, Set<String> scopes) {
        AccessToken token = new AccessToken(
                randomToken("access"),
                clientId,
                username,
                scopes,
                clock.now(),
                clock.now().plusSeconds(accessTokenTtlSeconds));
        accessTokens.put(token.getValue(), token);
        return token;
    }

    private RefreshToken issueRefreshToken(String clientId, String username, Set<String> scopes) {
        RefreshToken token = new RefreshToken(
                randomToken("refresh"),
                clientId,
                username,
                scopes,
                clock.now(),
                clock.now().plusSeconds(refreshTokenTtlSeconds));
        refreshTokens.put(token.getValue(), token);
        return token;
    }

    private OAuthClient authenticateClient(String clientId, String clientSecret) {
        OAuthClient client = requireClient(clientId);
        if (client.isConfidential() && !client.getClientSecret().equals(clientSecret)) {
            throw new OAuthException("invalid_client", "Client authentication failed");
        }
        return client;
    }

    private OAuthClient requireClient(String clientId) {
        OAuthClient client = clients.get(clientId);
        if (client == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
        return client;
    }

    private OAuthUser requireUser(String username) {
        OAuthUser user = users.get(username);
        if (user == null) {
            throw new OAuthException("access_denied", "Resource owner is not authenticated");
        }
        return user;
    }

    private static void validateRedirectUri(OAuthClient client, String redirectUri) {
        if (!client.getRedirectUri().equals(redirectUri)) {
            throw new OAuthException("invalid_request", "Invalid redirect_uri");
        }
    }

    private static void validateGrantType(OAuthClient client, String grantType) {
        if (!client.getGrantTypes().contains(grantType)) {
            throw new OAuthException("unauthorized_client", "Client is not authorized for grant type");
        }
    }

    private static void validateScopes(OAuthClient client, Set<String> requestedScopes) {
        for (String scope : requestedScopes) {
            if (!client.getScopes().contains(scope)) {
                throw new OAuthException("invalid_scope", "Scope is not allowed: " + scope);
            }
        }
    }

    private static Set<String> parseScopes(String scope) {
        Set<String> scopes = new LinkedHashSet<String>();
        if (scope == null || scope.trim().isEmpty()) {
            return scopes;
        }
        for (String value : scope.trim().split("\\s+")) {
            scopes.add(value);
        }
        return scopes;
    }

    private static String joinScopes(Set<String> scopes) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String scope : scopes) {
            if (!first) {
                builder.append(' ');
            }
            builder.append(scope);
            first = false;
        }
        return builder.toString();
    }

    private static String appendState(String state) {
        return state == null || state.trim().isEmpty() ? "" : "&state=" + state;
    }

    private static String randomToken(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "");
    }

    public interface Clock {
        LocalDateTime now();
    }

    private static final class SystemClock implements Clock {
        public LocalDateTime now() {
            return LocalDateTime.now();
        }
    }

    public static final class OAuthClient {
        private final String clientId;
        private final String clientSecret;
        private final String redirectUri;
        private final Set<String> scopes;
        private final Set<String> grantTypes;
        private final boolean confidential;

        private OAuthClient(String clientId, String clientSecret, String redirectUri, Set<String> scopes, Set<String> grantTypes, boolean confidential) {
            if (isBlank(clientId) || isBlank(redirectUri)) {
                throw new IllegalArgumentException("clientId and redirectUri are required");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret == null ? "" : clientSecret;
            this.redirectUri = redirectUri;
            this.scopes = scopes == null ? Collections.<String>emptySet() : Collections.unmodifiableSet(new LinkedHashSet<String>(scopes));
            this.grantTypes = grantTypes == null ? Collections.<String>emptySet() : Collections.unmodifiableSet(new LinkedHashSet<String>(grantTypes));
            this.confidential = confidential;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public Set<String> getGrantTypes() {
            return grantTypes;
        }

        public boolean isConfidential() {
            return confidential;
        }
    }

    public static final class OAuthUser {
        private final String username;
        private final String password;

        private OAuthUser(String username, String password) {
            if (isBlank(username) || isBlank(password)) {
                throw new IllegalArgumentException("username and password are required");
            }
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static final class AuthorizationCode {
        private final String code;
        private final String clientId;
        private final String username;
        private final String redirectUri;
        private final Set<String> scopes;
        private final LocalDateTime issuedAt;
        private final LocalDateTime expiresAt;
        private boolean used;

        private AuthorizationCode(String code, String clientId, String username, String redirectUri, Set<String> scopes, LocalDateTime issuedAt, LocalDateTime expiresAt) {
            this.code = code;
            this.clientId = clientId;
            this.username = username;
            this.redirectUri = redirectUri;
            this.scopes = Collections.unmodifiableSet(new LinkedHashSet<String>(scopes));
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        private boolean isExpired(LocalDateTime now) {
            return !now.isBefore(expiresAt);
        }

        private void markUsed() {
            used = true;
        }

        public String getCode() {
            return code;
        }

        public String getClientId() {
            return clientId;
        }

        public String getUsername() {
            return username;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public LocalDateTime getIssuedAt() {
            return issuedAt;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public boolean isUsed() {
            return used;
        }
    }

    public static final class AccessToken {
        private final String value;
        private final String clientId;
        private final String username;
        private final Set<String> scopes;
        private final LocalDateTime issuedAt;
        private final LocalDateTime expiresAt;
        private boolean revoked;

        private AccessToken(String value, String clientId, String username, Set<String> scopes, LocalDateTime issuedAt, LocalDateTime expiresAt) {
            this.value = value;
            this.clientId = clientId;
            this.username = username;
            this.scopes = Collections.unmodifiableSet(new LinkedHashSet<String>(scopes));
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        private boolean isExpired(LocalDateTime now) {
            return !now.isBefore(expiresAt);
        }

        private void revoke() {
            revoked = true;
        }

        public String getValue() {
            return value;
        }

        public String getClientId() {
            return clientId;
        }

        public String getUsername() {
            return username;
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public LocalDateTime getIssuedAt() {
            return issuedAt;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public boolean isRevoked() {
            return revoked;
        }
    }

    public static final class RefreshToken {
        private final String value;
        private final String clientId;
        private final String username;
        private final Set<String> scopes;
        private final LocalDateTime issuedAt;
        private final LocalDateTime expiresAt;
        private boolean revoked;

        private RefreshToken(String value, String clientId, String username, Set<String> scopes, LocalDateTime issuedAt, LocalDateTime expiresAt) {
            this.value = value;
            this.clientId = clientId;
            this.username = username;
            this.scopes = Collections.unmodifiableSet(new LinkedHashSet<String>(scopes));
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        private boolean isExpired(LocalDateTime now) {
            return !now.isBefore(expiresAt);
        }

        public String getValue() {
            return value;
        }

        public String getClientId() {
            return clientId;
        }

        public String getUsername() {
            return username;
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public LocalDateTime getIssuedAt() {
            return issuedAt;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public boolean isRevoked() {
            return revoked;
        }
    }

    public static final class AuthorizationResponse {
        private final String code;
        private final String state;
        private final String redirectLocation;

        private AuthorizationResponse(String code, String state, String redirectLocation) {
            this.code = code;
            this.state = state;
            this.redirectLocation = redirectLocation;
        }

        public String getCode() {
            return code;
        }

        public String getState() {
            return state;
        }

        public String getRedirectLocation() {
            return redirectLocation;
        }
    }

    public static final class TokenRequest {
        private final String grantType;
        private final String clientId;
        private final String clientSecret;
        private String code;
        private String redirectUri;
        private String refreshToken;
        private String username;
        private String password;
        private String scope;

        private TokenRequest(String grantType, String clientId, String clientSecret) {
            this.grantType = grantType;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public TokenRequest code(String code) {
            this.code = code;
            return this;
        }

        public TokenRequest redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public TokenRequest refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenRequest username(String username) {
            this.username = username;
            return this;
        }

        public TokenRequest password(String password) {
            this.password = password;
            return this;
        }

        public TokenRequest scope(String scope) {
            this.scope = scope;
            return this;
        }

        public String getGrantType() {
            return grantType;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getCode() {
            return code;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getScope() {
            return scope;
        }
    }

    public static final class TokenResponse {
        private final String accessToken;
        private final String tokenType;
        private final long expiresIn;
        private final String refreshToken;
        private final String scope;

        private TokenResponse(String accessToken, String tokenType, long expiresIn, String refreshToken, String scope) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.refreshToken = refreshToken;
            this.scope = scope;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getScope() {
            return scope;
        }
    }

    public static final class OAuthConfig {
        private final long authorizationCodeTtlSeconds;
        private final long accessTokenTtlSeconds;
        private final long refreshTokenTtlSeconds;

        private OAuthConfig(long authorizationCodeTtlSeconds, long accessTokenTtlSeconds, long refreshTokenTtlSeconds) {
            this.authorizationCodeTtlSeconds = authorizationCodeTtlSeconds;
            this.accessTokenTtlSeconds = accessTokenTtlSeconds;
            this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
        }

        public long getAuthorizationCodeTtlSeconds() {
            return authorizationCodeTtlSeconds;
        }

        public long getAccessTokenTtlSeconds() {
            return accessTokenTtlSeconds;
        }

        public long getRefreshTokenTtlSeconds() {
            return refreshTokenTtlSeconds;
        }
    }

    public static final class OAuthException extends RuntimeException {
        private final String error;

        private OAuthException(String error, String description) {
            super(description);
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public String getErrorDescription() {
            return getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
