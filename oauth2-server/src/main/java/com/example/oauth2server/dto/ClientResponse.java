package com.example.oauth2server.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.example.oauth2server.entity.OAuthClient;

public class ClientResponse {

    private Long id;
    private String clientId;
    private String name;
    private String redirectUri;
    private List<String> scopes;
    private List<String> authorizedGrantTypes;
    private boolean confidential;
    private LocalDateTime createdAt;

    public static ClientResponse from(OAuthClient client) {
        ClientResponse response = new ClientResponse();
        response.id = client.getId();
        response.clientId = client.getClientId();
        response.name = client.getName();
        response.redirectUri = client.getRedirectUri();
        response.scopes = Arrays.asList(client.getScopes().split(" "));
        response.authorizedGrantTypes = Arrays.asList(client.getAuthorizedGrantTypes().split(" "));
        response.confidential = client.isConfidential();
        response.createdAt = client.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public List<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public boolean isConfidential() {
        return confidential;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
