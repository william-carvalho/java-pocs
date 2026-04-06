package com.example.oauth2server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.oauth2server.entity.AuthorizationCode;
import com.example.oauth2server.repository.AccessTokenRepository;
import com.example.oauth2server.repository.AuthorizationCodeRepository;
import com.example.oauth2server.repository.RefreshTokenRepository;

@SpringBootTest
@AutoConfigureMockMvc
class Oauth2ServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        accessTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        authorizationCodeRepository.deleteAll();
    }

    @Test
    void shouldGenerateAuthorizationCodeAndRedirectWithState() throws Exception {
        mockMvc.perform(get("/oauth/authorize")
                        .param("response_type", "code")
                        .param("client_id", "client-app")
                        .param("redirect_uri", "http://localhost:8081/callback")
                        .param("scope", "read")
                        .param("state", "xyz")
                        .param("username", "william"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("code=")))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("state=xyz")));

        assertThat(authorizationCodeRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldIssueTokenForAuthorizationCode() throws Exception {
        MvcResult result = mockMvc.perform(get("/oauth/authorize")
                        .param("response_type", "code")
                        .param("client_id", "client-app")
                        .param("redirect_uri", "http://localhost:8081/callback")
                        .param("scope", "read")
                        .param("username", "william"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        String code = location.substring(location.indexOf("code=") + 5);

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", code)
                        .param("redirect_uri", "http://localhost:8081/callback")
                        .param("client_id", "client-app")
                        .param("client_secret", "secret123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").value("bearer"));

        assertThat(accessTokenRepository.count()).isEqualTo(1);
        assertThat(refreshTokenRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldNotReuseAuthorizationCode() throws Exception {
        AuthorizationCode code = new AuthorizationCode();
        code.setCode("used-code");
        code.setClientId("client-app");
        code.setUsername("william");
        code.setRedirectUri("http://localhost:8081/callback");
        code.setScope("read");
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        code.setConsumed(true);
        authorizationCodeRepository.save(code);

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", "used-code")
                        .param("redirect_uri", "http://localhost:8081/callback")
                        .param("client_id", "client-app")
                        .param("client_secret", "secret123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    void shouldRejectExpiredAuthorizationCode() throws Exception {
        AuthorizationCode code = new AuthorizationCode();
        code.setCode("expired-code");
        code.setClientId("client-app");
        code.setUsername("william");
        code.setRedirectUri("http://localhost:8081/callback");
        code.setScope("read");
        code.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        code.setConsumed(false);
        authorizationCodeRepository.save(code);

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", "expired-code")
                        .param("redirect_uri", "http://localhost:8081/callback")
                        .param("client_id", "client-app")
                        .param("client_secret", "secret123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    void shouldIssueClientCredentialsToken() throws Exception {
        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "service-client")
                        .param("client_secret", "service-secret")
                        .param("scope", "read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.scope").value("read"));
    }

    @Test
    void shouldReturnInvalidClientForWrongSecret() throws Exception {
        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("client_id", "service-client")
                        .param("client_secret", "wrong-secret")
                        .param("scope", "read"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_client"));
    }

    @Test
    void shouldFailForInvalidRedirectUri() throws Exception {
        mockMvc.perform(get("/oauth/authorize")
                        .param("response_type", "code")
                        .param("client_id", "client-app")
                        .param("redirect_uri", "http://localhost:8081/other")
                        .param("scope", "read")
                        .param("username", "william"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"));
    }
}
