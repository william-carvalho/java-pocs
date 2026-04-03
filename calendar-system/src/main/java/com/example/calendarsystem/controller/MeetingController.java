package com.example.calendarsystem.controller;

import com.example.calendarsystem.dto.CreateMeetingRequest;
import com.example.calendarsystem.dto.MeetingResponse;
import com.example.calendarsystem.dto.SuggestMeetingRequest;
import com.example.calendarsystem.dto.SuggestMeetingResponse;
import com.example.calendarsystem.entity.MeetingStatus;
import com.example.calendarsystem.service.MeetingService;
import com.example.calendarsystem.service.MeetingSuggestionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingSuggestionService meetingSuggestionService;

    public MeetingController(MeetingService meetingService, MeetingSuggestionService meetingSuggestionService) {
        this.meetingService = meetingService;
        this.meetingSuggestionService = meetingSuggestionService;
    }

    @PostMapping
    public ResponseEntity<MeetingResponse> create(@Valid @RequestBody CreateMeetingRequest request) {
        return ResponseEntity.ok(meetingService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> list(
            @RequestParam(value = "personId", required = false) Long personId,
            @RequestParam(value = "status", required = false) MeetingStatus status,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(meetingService.list(personId, status, date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        meetingService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggest")
    public ResponseEntity<SuggestMeetingResponse> suggest(@Valid @RequestBody SuggestMeetingRequest request) {
        return ResponseEntity.ok(meetingSuggestionService.suggest(request));
    }
}

