package com.example.calendarsystem.controller;

import com.example.calendarsystem.dto.PersonRequest;
import com.example.calendarsystem.dto.PersonResponse;
import com.example.calendarsystem.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        return ResponseEntity.ok(personService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponse>> listAll() {
        return ResponseEntity.ok(personService.listAll());
    }
}

