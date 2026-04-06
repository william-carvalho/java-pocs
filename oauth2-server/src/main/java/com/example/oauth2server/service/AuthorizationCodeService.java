package com.example.oauth2server.service;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.oauth2server.config.OAuthProperties;
import com.example.oauth2server.entity.AuthorizationCode;
import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.repository.AuthorizationCodeRepository;
import com.example.oauth2server.security.OAuthRandomTokenGenerator;

@Service
public class AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final OAuthRandomTokenGenerator tokenGenerator;
    private final OAuthProperties properties;

    public AuthorizationCodeService(AuthorizationCodeRepository authorizationCodeRepository,
                                    OAuthRandomTokenGenerator tokenGenerator,
                                    OAuthProperties properties) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.tokenGenerator = tokenGenerator;
        this.properties = properties;
    }

    public ResponseEntity<Void> issueAuthorizationCode(OAuthClient client,
                                                       String username,
                                                       String redirectUri,
                                                       String scope,
                                                       String state) {
        AuthorizationCode code = new AuthorizationCode();
        code.setCode(tokenGenerator.generateToken().substring(0, 32));
        code.setClientId(client.getClientId());
        code.setUsername(username);
        code.setRedirectUri(redirectUri);
        code.setScope(scope);
        code.setConsumed(false);
        code.setExpiresAt(LocalDateTime.now().plusSeconds(properties.getAuthorizationCodeExpirationSeconds()));
        authorizationCodeRepository.save(code);

        URI uri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", code.getCode())
                .queryParamIfPresent("state", java.util.Optional.ofNullable(state))
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
    }

    public AuthorizationCode consume(String codeValue, String clientId, String redirectUri) {
        AuthorizationCode code = authorizationCodeRepository.findByCode(codeValue)
                .orElseThrow(() -> new OAuthException("invalid_grant", "Authorization code is invalid or expired",
                        HttpStatus.BAD_REQUEST));

        if (code.isConsumed() || code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OAuthException("invalid_grant", "Authorization code is invalid or expired",
                    HttpStatus.BAD_REQUEST);
        }
        if (!code.getClientId().equals(clientId)) {
            throw new OAuthException("invalid_grant", "Authorization code does not belong to this client",
                    HttpStatus.BAD_REQUEST);
        }
        if (!code.getRedirectUri().equals(redirectUri)) {
            throw new OAuthException("invalid_grant", "redirect_uri does not match authorization code",
                    HttpStatus.BAD_REQUEST);
        }

        code.setConsumed(true);
        return authorizationCodeRepository.save(code);
    }
}
