package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.ClassSessionResponse;
import com.example.teachersclassorganizeroptimizer.dto.CreateClassSessionRequest;
import com.example.teachersclassorganizeroptimizer.dto.SuggestScheduleRequest;
import com.example.teachersclassorganizeroptimizer.dto.SuggestScheduleResponse;
import com.example.teachersclassorganizeroptimizer.entity.SessionStatus;
import com.example.teachersclassorganizeroptimizer.service.ClassSessionService;
import com.example.teachersclassorganizeroptimizer.service.SessionSuggestionService;
import java.time.DayOfWeek;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class ClassSessionController {

    private final ClassSessionService classSessionService;
    private final SessionSuggestionService sessionSuggestionService;

    public ClassSessionController(ClassSessionService classSessionService,
                                  SessionSuggestionService sessionSuggestionService) {
        this.classSessionService = classSessionService;
        this.sessionSuggestionService = sessionSuggestionService;
    }

    @PostMapping
    public ResponseEntity<ClassSessionResponse> create(@Valid @RequestBody CreateClassSessionRequest request) {
        return ResponseEntity.ok(classSessionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ClassSessionResponse>> list(
            @RequestParam(value = "teacherId", required = false) Long teacherId,
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "roomId", required = false) Long roomId,
            @RequestParam(value = "dayOfWeek", required = false) DayOfWeek dayOfWeek,
            @RequestParam(value = "status", required = false) SessionStatus status) {
        return ResponseEntity.ok(classSessionService.list(teacherId, classId, roomId, dayOfWeek, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassSessionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(classSessionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        classSessionService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggest")
    public ResponseEntity<SuggestScheduleResponse> suggest(@Valid @RequestBody SuggestScheduleRequest request) {
        return ResponseEntity.ok(sessionSuggestionService.suggest(request));
    }
}

