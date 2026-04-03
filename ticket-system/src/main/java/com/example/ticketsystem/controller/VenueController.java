package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.VenueRequest;
import com.example.ticketsystem.dto.VenueResponse;
import com.example.ticketsystem.dto.VenueZoneRequest;
import com.example.ticketsystem.dto.VenueZoneResponse;
import com.example.ticketsystem.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody VenueRequest request) {
        return service.createVenue(request);
    }

    @GetMapping
    public List<VenueResponse> listVenues() {
        return service.listVenues();
    }

    @PostMapping("/{venueId}/zones")
    @ResponseStatus(HttpStatus.CREATED)
    public VenueZoneResponse createZone(@PathVariable("venueId") Long venueId,
                                        @Valid @RequestBody VenueZoneRequest request) {
        return service.createZone(venueId, request);
    }

    @GetMapping("/{venueId}/zones")
    public List<VenueZoneResponse> listZones(@PathVariable("venueId") Long venueId) {
        return service.listZones(venueId);
    }
}
