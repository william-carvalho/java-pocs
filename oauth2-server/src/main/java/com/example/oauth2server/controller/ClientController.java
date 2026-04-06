package com.example.oauth2server.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2server.dto.ClientRegistrationRequest;
import com.example.oauth2server.dto.ClientResponse;
import com.example.oauth2server.service.OAuthClientService;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final OAuthClientService oAuthClientService;

    public ClientController(OAuthClientService oAuthClientService) {
        this.oAuthClientService = oAuthClientService;
    }

    @PostMapping
    public ClientResponse create(@Valid @RequestBody ClientRegistrationRequest request) {
        return oAuthClientService.create(request);
    }

    @GetMapping
    public List<ClientResponse> listAll() {
        return oAuthClientService.listAll();
    }
}
