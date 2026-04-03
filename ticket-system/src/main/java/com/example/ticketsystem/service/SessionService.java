package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.ShowSessionRequest;
import com.example.ticketsystem.dto.ShowSessionResponse;
import com.example.ticketsystem.entity.ShowEvent;
import com.example.ticketsystem.entity.ShowSession;
import com.example.ticketsystem.entity.Venue;
import com.example.ticketsystem.exception.BusinessValidationException;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.repository.ShowSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final ShowSessionRepository repository;
    private final ShowService showService;
    private final VenueService venueService;

    public SessionService(ShowSessionRepository repository, ShowService showService, VenueService venueService) {
        this.repository = repository;
        this.showService = showService;
        this.venueService = venueService;
    }

    @Transactional
    public ShowSessionResponse create(ShowSessionRequest request) {
        ShowEvent show = showService.findOrThrow(request.getShowId());
        Venue venue = venueService.findVenueOrThrow(request.getVenueId());
        int totalCapacity = venueService.calculateVenueCapacity(venue.getId());

        if (totalCapacity <= 0) {
            throw new BusinessValidationException("Cannot create session for a venue without configured zones");
        }

        ShowSession entity = new ShowSession();
        entity.setShow(show);
        entity.setVenue(venue);
        entity.setEventDateTime(request.getEventDateTime());
        entity.setTotalCapacity(totalCapacity);
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ShowSessionResponse> list(Long showId, LocalDate date) {
        return repository.findAllByOrderByEventDateTimeAscIdAsc()
                .stream()
                .filter(session -> showId == null || session.getShow().getId().equals(showId))
                .filter(session -> date == null || session.getEventDateTime().toLocalDate().equals(date))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowSession findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found for id " + id));
    }

    public ShowSessionResponse toResponse(ShowSession entity) {
        ShowSessionResponse response = new ShowSessionResponse();
        response.setId(entity.getId());
        response.setShowId(entity.getShow().getId());
        response.setShowName(entity.getShow().getName());
        response.setVenueId(entity.getVenue().getId());
        response.setVenueName(entity.getVenue().getName());
        response.setEventDateTime(entity.getEventDateTime());
        response.setTotalCapacity(entity.getTotalCapacity());
        return response;
    }
}
