package com.example.loggerbuilderroutersystem.controller;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.dto.LogResponse;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.enums.LogMode;
import com.example.loggerbuilderroutersystem.service.LogService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<LogResponse> publish(@Valid @RequestBody LogRequest request) {
        return ResponseEntity.ok(logService.publish(request));
    }

    @GetMapping("/destinations")
    public ResponseEntity<List<String>> destinations() {
        List<String> destinations = Arrays.stream(LogDestination.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/modes")
    public ResponseEntity<List<String>> modes() {
        List<String> modes = Arrays.stream(LogMode.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modes);
    }
}
