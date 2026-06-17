# OAuth2 Server Single Class

Java 8 POC for an OAuth 2.0 authorization server inspired by RFC 6749.

The production code is intentionally in one class:

```text
src/main/java/com/example/oauth2/OAuth2Server.java
```

## Implemented Scope

- Client registration.
- User registration.
- Authorization endpoint behavior for `response_type=code`.
- Token endpoint behavior for:
  - `authorization_code`
  - `client_credentials`
  - `password`
  - `refresh_token`
- Bearer access token issuance.
- Refresh token issuance for user-based grants.
- Access token revocation.
- OAuth-style errors such as `invalid_client`, `invalid_grant`, `invalid_scope`, and `unauthorized_client`.

## Example

```java
OAuth2Server server = OAuth2Server.withDefaultData();

OAuth2Server.AuthorizationResponse authorization = server.authorize(
        "code",
        "client-app",
        "http://localhost:8081/callback",
        "read",
        "state-1",
        "william");

OAuth2Server.TokenResponse token = server.token(
        OAuth2Server.tokenRequest("authorization_code", "client-app", "secret123")
                .code(authorization.getCode())
                .redirectUri("http://localhost:8081/callback"));
```

## Notes

This is a compact POC, not a production OAuth server. It intentionally omits TLS handling, PKCE, JWT, persistent storage, consent UI, token introspection, and OpenID Connect.

Reference: RFC 6749, The OAuth 2.0 Authorization Framework.

## Test

```bash
mvn test
```
