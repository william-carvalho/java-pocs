package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.TeacherRequest;
import com.example.teachersclassorganizeroptimizer.dto.TeacherResponse;
import com.example.teachersclassorganizeroptimizer.service.TeacherService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> list() {
        return ResponseEntity.ok(teacherService.listAll());
    }
}

