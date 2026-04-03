package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.ShowSessionRequest;
import com.example.ticketsystem.dto.ShowSessionResponse;
import com.example.ticketsystem.service.SessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShowSessionResponse create(@Valid @RequestBody ShowSessionRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ShowSessionResponse> list(
            @RequestParam(required = false) Long showId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.list(showId, date);
    }
}
