package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.AvailabilityResponse;
import com.example.ticketsystem.service.AvailabilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping("/{sessionId}/availability")
    public AvailabilityResponse getAvailability(@PathVariable("sessionId") Long sessionId) {
        return service.getAvailability(sessionId);
    }
}
