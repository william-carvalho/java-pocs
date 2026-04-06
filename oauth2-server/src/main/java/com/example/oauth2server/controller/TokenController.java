package com.example.oauth2server.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2server.config.OAuthProperties;
import com.example.oauth2server.dto.ConfigResponse;
import com.example.oauth2server.dto.RevokeTokenRequest;
import com.example.oauth2server.dto.TokenResponse;
import com.example.oauth2server.dto.TokenSummaryResponse;
import com.example.oauth2server.entity.AuthorizationCode;
import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.service.AuthorizationCodeService;
import com.example.oauth2server.service.OAuthClientService;
import com.example.oauth2server.service.TokenService;

@RestController
public class TokenController {

    private final OAuthClientService oAuthClientService;
    private final AuthorizationCodeService authorizationCodeService;
    private final TokenService tokenService;
    private final OAuthProperties properties;

    public TokenController(OAuthClientService oAuthClientService,
                           AuthorizationCodeService authorizationCodeService,
                           TokenService tokenService,
                           OAuthProperties properties) {
        this.oAuthClientService = oAuthClientService;
        this.authorizationCodeService = authorizationCodeService;
        this.tokenService = tokenService;
        this.properties = properties;
    }

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenResponse token(@RequestParam("grant_type") String grantType,
                               @RequestParam("client_id") String clientId,
                               @RequestParam(name = "client_secret", required = false) String clientSecret,
                               @RequestParam(name = "code", required = false) String code,
                               @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                               @RequestParam(name = "scope", required = false) String scope,
                               @RequestParam(name = "refresh_token", required = false) String refreshToken) {
        OAuthClient client = oAuthClientService.getRequiredClient(clientId);
        oAuthClientService.authenticateClient(client, clientSecret);
        oAuthClientService.validateGrantType(client, grantType);

        if ("authorization_code".equals(grantType)) {
            if (code == null || redirectUri == null) {
                throw new OAuthException("invalid_request",
                        "code and redirect_uri are required for authorization_code",
                        org.springframework.http.HttpStatus.BAD_REQUEST);
            }
            AuthorizationCode authorizationCode = authorizationCodeService.consume(code, client.getClientId(), redirectUri);
            return tokenService.issueForAuthorizationCode(client, authorizationCode);
        }

        if ("client_credentials".equals(grantType)) {
            String resolvedScope = oAuthClientService.validateRequestedScope(client, scope);
            return tokenService.issueForClientCredentials(client, resolvedScope);
        }

        if ("refresh_token".equals(grantType)) {
            if (refreshToken == null) {
                throw new OAuthException("invalid_request",
                        "refresh_token is required for refresh_token grant",
                        org.springframework.http.HttpStatus.BAD_REQUEST);
            }
            return tokenService.issueFromRefreshToken(client, refreshToken);
        }

        throw new OAuthException("unsupported_grant_type", "Grant type is not supported",
                org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/tokens")
    public List<TokenSummaryResponse> listTokens() {
        return tokenService.listAll();
    }

    @PostMapping(value = "/tokens/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void revokeToken(@Valid @ModelAttribute RevokeTokenRequest request) {
        tokenService.revoke(request.getToken());
    }

    @GetMapping("/config")
    public ConfigResponse config() {
        return new ConfigResponse(
                properties.getAccessTokenExpirationSeconds(),
                properties.getAuthorizationCodeExpirationSeconds(),
                properties.getRefreshTokenExpirationSeconds(),
                new String[]{"authorization_code", "client_credentials", "refresh_token"});
    }
}
