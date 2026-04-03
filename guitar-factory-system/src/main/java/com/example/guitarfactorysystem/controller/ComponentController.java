package com.example.guitarfactorysystem.controller;

import com.example.guitarfactorysystem.dto.ComponentRequest;
import com.example.guitarfactorysystem.dto.ComponentResponse;
import com.example.guitarfactorysystem.service.ComponentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/components")
public class ComponentController {

    private final ComponentService service;

    public ComponentController(ComponentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentResponse create(@Valid @RequestBody ComponentRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ComponentResponse> list() {
        return service.list();
    }
}
