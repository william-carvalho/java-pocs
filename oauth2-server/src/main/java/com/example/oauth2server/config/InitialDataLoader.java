package com.example.oauth2server.config;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.entity.OAuthUser;
import com.example.oauth2server.repository.OAuthClientRepository;
import com.example.oauth2server.repository.OAuthUserRepository;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final OAuthClientRepository oAuthClientRepository;
    private final OAuthUserRepository oAuthUserRepository;

    public InitialDataLoader(OAuthClientRepository oAuthClientRepository,
                             OAuthUserRepository oAuthUserRepository) {
        this.oAuthClientRepository = oAuthClientRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Override
    public void run(String... args) {
        if (oAuthUserRepository.count() == 0) {
            OAuthUser william = new OAuthUser();
            william.setUsername("william");
            william.setPassword("123456");
            william.setActive(true);

            OAuthUser maria = new OAuthUser();
            maria.setUsername("maria");
            maria.setPassword("123456");
            maria.setActive(true);

            oAuthUserRepository.saveAll(Arrays.asList(william, maria));
        }

        if (oAuthClientRepository.count() == 0) {
            OAuthClient clientApp = new OAuthClient();
            clientApp.setClientId("client-app");
            clientApp.setClientSecret("secret123");
            clientApp.setName("Client App");
            clientApp.setRedirectUri("http://localhost:8081/callback");
            clientApp.setScopes("read write");
            clientApp.setAuthorizedGrantTypes("authorization_code refresh_token");
            clientApp.setConfidential(true);
            clientApp.setCreatedAt(LocalDateTime.now());

            OAuthClient serviceClient = new OAuthClient();
            serviceClient.setClientId("service-client");
            serviceClient.setClientSecret("service-secret");
            serviceClient.setName("Service Client");
            serviceClient.setRedirectUri("http://localhost:8081/callback");
            serviceClient.setScopes("read");
            serviceClient.setAuthorizedGrantTypes("client_credentials");
            serviceClient.setConfidential(true);
            serviceClient.setCreatedAt(LocalDateTime.now());

            oAuthClientRepository.saveAll(Arrays.asList(clientApp, serviceClient));
        }
    }
}
