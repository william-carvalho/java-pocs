package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.StudentRequest;
import com.example.teachersclassorganizeroptimizer.dto.StudentResponse;
import com.example.teachersclassorganizeroptimizer.service.StudentService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> list(@RequestParam(value = "classId", required = false) Long classId) {
        return ResponseEntity.ok(studentService.listAll(classId));
    }
}

