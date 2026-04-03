package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.SubjectRequest;
import com.example.teachersclassorganizeroptimizer.dto.SubjectResponse;
import com.example.teachersclassorganizeroptimizer.service.SubjectService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(subjectService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> list() {
        return ResponseEntity.ok(subjectService.listAll());
    }
}

