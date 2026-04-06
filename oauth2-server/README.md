# OAuth2 Server

POC simples em Java 8 com Spring Boot para demonstrar um Authorization Server inspirado no RFC 6749.

## O que foi implementado

Subconjunto pragmatico do OAuth 2.0:

- cadastro simples de clients
- `GET /oauth/authorize`
- `POST /oauth/token`
- grant `authorization_code`
- grant `client_credentials`
- grant `refresh_token`
- emissao de `access_token`
- emissao de `refresh_token` no fluxo `authorization_code`
- persistencia em H2 para clients, users, authorization codes e tokens
- listagem de tokens para debug
- revogacao simples de access token

## O que ficou de fora

- OpenID Connect
- PKCE
- consent screen real
- login real com sessao
- JWT
- introspection endpoint
- revogacao completa de refresh token chain

## Simplificacao importante da POC

O `authorization endpoint` usa `username` como query param para simular um usuario ja autenticado.

Exemplo:

```bash
curl -i "http://localhost:8080/oauth/authorize?response_type=code&client_id=client-app&redirect_uri=http://localhost:8081/callback&scope=read&state=abc&username=william"
```

O servidor responde com `302 Found` e devolve `code` e `state` na `Location`.

## Como rodar

```bash
mvn spring-boot:run
```

## Dados iniciais

Users:

- `william / 123456`
- `maria / 123456`

Clients:

- `client-app / secret123`
- `service-client / service-secret`

## Endpoints

### Criar client

```bash
curl -X POST http://localhost:8080/clients \
-H "Content-Type: application/json" \
-d '{
  "clientId": "custom-client",
  "clientSecret": "custom-secret",
  "name": "Custom Client",
  "redirectUri": "http://localhost:8081/callback",
  "scopes": ["read","write"],
  "authorizedGrantTypes": ["authorization_code","refresh_token"],
  "confidential": true
}'
```

### Listar clients

```bash
curl http://localhost:8080/clients
```

### Authorization code flow

```bash
curl -i "http://localhost:8080/oauth/authorize?response_type=code&client_id=client-app&redirect_uri=http://localhost:8081/callback&scope=read&state=xyz&username=william"
```

Depois troque o `code` por token:

```bash
curl -X POST http://localhost:8080/oauth/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=authorization_code&code=AUTH_CODE&redirect_uri=http://localhost:8081/callback&client_id=client-app&client_secret=secret123"
```

### Client credentials flow

```bash
curl -X POST http://localhost:8080/oauth/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=client_credentials&client_id=service-client&client_secret=service-secret&scope=read"
```

### Refresh token flow

```bash
curl -X POST http://localhost:8080/oauth/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=refresh_token&refresh_token=REFRESH_TOKEN&client_id=client-app&client_secret=secret123"
```

### Listar tokens

```bash
curl http://localhost:8080/tokens
```

### Revogar token

```bash
curl -X POST http://localhost:8080/tokens/revoke \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "token=ACCESS_TOKEN"
```

### Config

```bash
curl http://localhost:8080/config
```

## Erros OAuth

```json
{
  "error": "invalid_client",
  "error_description": "Client authentication failed"
}
```

```json
{
  "error": "invalid_grant",
  "error_description": "Authorization code is invalid or expired"
}
```

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:oauthdb`
- User: `sa`
- Password: vazio

## Testes

```bash
mvn test
```

Cobertura principal:

- geracao de authorization code
- expiracao e reuso de code
- emissao de token para `authorization_code`
- emissao de token para `client_credentials`
- erro `invalid_client`
- erro para `redirect_uri` invalido
- preservacao de `state`

## Observacao final

O projeto e inspirado no RFC 6749, mas deliberadamente implementa apenas um subconjunto consistente para demonstracao rapida e clara.
