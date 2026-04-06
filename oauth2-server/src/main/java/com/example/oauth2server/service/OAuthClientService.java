package com.example.oauth2server.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.oauth2server.dto.ClientRegistrationRequest;
import com.example.oauth2server.dto.ClientResponse;
import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.repository.OAuthClientRepository;

@Service
public class OAuthClientService {

    private final OAuthClientRepository oAuthClientRepository;

    public OAuthClientService(OAuthClientRepository oAuthClientRepository) {
        this.oAuthClientRepository = oAuthClientRepository;
    }

    public ClientResponse create(ClientRegistrationRequest request) {
        if (oAuthClientRepository.findByClientId(request.getClientId()).isPresent()) {
            throw new OAuthException("invalid_client_metadata", "client_id already exists", HttpStatus.BAD_REQUEST);
        }
        if (request.isConfidential() && isBlank(request.getClientSecret())) {
            throw new OAuthException("invalid_client_metadata", "client_secret is required for confidential clients",
                    HttpStatus.BAD_REQUEST);
        }

        OAuthClient client = new OAuthClient();
        client.setClientId(request.getClientId());
        client.setClientSecret(request.getClientSecret());
        client.setName(request.getName());
        client.setRedirectUri(request.getRedirectUri());
        client.setScopes(join(request.getScopes()));
        client.setAuthorizedGrantTypes(join(request.getAuthorizedGrantTypes()));
        client.setConfidential(request.isConfidential());
        return ClientResponse.from(oAuthClientRepository.save(client));
    }

    public List<ClientResponse> listAll() {
        return oAuthClientRepository.findAll()
                .stream()
                .map(ClientResponse::from)
                .collect(Collectors.toList());
    }

    public OAuthClient getRequiredClient(String clientId) {
        return oAuthClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new OAuthException("invalid_client", "Client not found", HttpStatus.UNAUTHORIZED));
    }

    public void validateRedirectUri(OAuthClient client, String redirectUri) {
        if (isBlank(redirectUri) || !client.getRedirectUri().equals(redirectUri)) {
            throw new OAuthException("invalid_request", "redirect_uri does not match registered client",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void validateGrantType(OAuthClient client, String grantType) {
        Set<String> allowed = splitToSet(client.getAuthorizedGrantTypes());
        if (!allowed.contains(grantType)) {
            throw new OAuthException("unauthorized_client", "Grant type is not allowed for this client",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public String validateRequestedScope(OAuthClient client, String requestedScope) {
        Set<String> allowed = splitToSet(client.getScopes());
        if (isBlank(requestedScope)) {
            return client.getScopes();
        }

        Set<String> requested = splitToSet(requestedScope);
        if (!allowed.containsAll(requested)) {
            throw new OAuthException("invalid_scope", "Requested scope is invalid", HttpStatus.BAD_REQUEST);
        }
        return requested.stream().collect(Collectors.joining(" "));
    }

    public void authenticateClient(OAuthClient client, String clientSecret) {
        if (client.isConfidential() && !client.getClientSecret().equals(clientSecret)) {
            throw new OAuthException("invalid_client", "Client authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private Set<String> splitToSet(String value) {
        return java.util.Arrays.stream(value.split(" "))
                .filter(item -> !item.trim().isEmpty())
                .collect(Collectors.toSet());
    }

    private String join(List<String> values) {
        return values.stream()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
