package com.example.guitarfactorysystem.controller;

import com.example.guitarfactorysystem.dto.CreateCustomGuitarRequest;
import com.example.guitarfactorysystem.dto.CustomGuitarOrderResponse;
import com.example.guitarfactorysystem.service.CustomGuitarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/custom-guitars")
public class CustomGuitarController {

    private final CustomGuitarService service;

    public CustomGuitarController(CustomGuitarService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomGuitarOrderResponse create(@Valid @RequestBody CreateCustomGuitarRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<CustomGuitarOrderResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CustomGuitarOrderResponse get(@PathVariable("id") Long id) {
        return service.get(id);
    }
}
