package com.example.socialmediaphotoapp.controller;

import com.example.socialmediaphotoapp.dto.TimelineResponse;
import com.example.socialmediaphotoapp.service.TimelineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeline")
public class TimelineController {

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping
    public ResponseEntity<TimelineResponse> timeline(@RequestParam(value = "userId", required = false) Long userId,
                                                     @RequestParam(value = "tag", required = false) String tag,
                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(timelineService.timeline(userId, tag, page, size));
    }
}

