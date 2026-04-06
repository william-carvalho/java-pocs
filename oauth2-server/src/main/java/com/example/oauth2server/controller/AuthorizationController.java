package com.example.oauth2server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2server.entity.OAuthClient;
import com.example.oauth2server.exception.OAuthException;
import com.example.oauth2server.service.AuthorizationCodeService;
import com.example.oauth2server.service.OAuthClientService;
import com.example.oauth2server.service.OAuthUserService;

@RestController
@RequestMapping("/oauth")
public class AuthorizationController {

    private final OAuthClientService oAuthClientService;
    private final OAuthUserService oAuthUserService;
    private final AuthorizationCodeService authorizationCodeService;

    public AuthorizationController(OAuthClientService oAuthClientService,
                                   OAuthUserService oAuthUserService,
                                   AuthorizationCodeService authorizationCodeService) {
        this.oAuthClientService = oAuthClientService;
        this.oAuthUserService = oAuthUserService;
        this.authorizationCodeService = authorizationCodeService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize(@RequestParam(name = "response_type") String responseType,
                                          @RequestParam(name = "client_id") String clientId,
                                          @RequestParam(name = "redirect_uri") String redirectUri,
                                          @RequestParam(name = "username") String username,
                                          @RequestParam(name = "scope", required = false) String scope,
                                          @RequestParam(name = "state", required = false) String state) {
        if (!"code".equals(responseType)) {
            throw new OAuthException("unsupported_response_type",
                    "Only response_type=code is supported", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        OAuthClient client = oAuthClientService.getRequiredClient(clientId);
        oAuthClientService.validateGrantType(client, "authorization_code");
        oAuthClientService.validateRedirectUri(client, redirectUri);
        String resolvedScope = oAuthClientService.validateRequestedScope(client, scope);
        oAuthUserService.getActiveUser(username);

        return authorizationCodeService.issueAuthorizationCode(client, username, redirectUri, resolvedScope, state);
    }
}
