package com.example.guitarfactorysystem.controller;

import com.example.guitarfactorysystem.dto.GuitarModelRequest;
import com.example.guitarfactorysystem.dto.GuitarModelResponse;
import com.example.guitarfactorysystem.service.GuitarModelService;
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
@RequestMapping("/models")
public class GuitarModelController {

    private final GuitarModelService service;

    public GuitarModelController(GuitarModelService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuitarModelResponse create(@Valid @RequestBody GuitarModelRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<GuitarModelResponse> list() {
        return service.list();
    }
}
