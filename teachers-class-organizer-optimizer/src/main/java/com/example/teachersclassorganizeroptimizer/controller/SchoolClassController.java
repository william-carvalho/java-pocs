package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.SchoolClassRequest;
import com.example.teachersclassorganizeroptimizer.dto.SchoolClassResponse;
import com.example.teachersclassorganizeroptimizer.service.SchoolClassService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @PostMapping
    public ResponseEntity<SchoolClassResponse> create(@Valid @RequestBody SchoolClassRequest request) {
        return ResponseEntity.ok(schoolClassService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SchoolClassResponse>> list() {
        return ResponseEntity.ok(schoolClassService.listAll());
    }
}

