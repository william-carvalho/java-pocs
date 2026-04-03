package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.SellTicketItemRequest;
import com.example.ticketsystem.dto.SellTicketRequest;
import com.example.ticketsystem.dto.SellTicketResponse;
import com.example.ticketsystem.dto.TicketResponse;
import com.example.ticketsystem.entity.ShowSession;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.entity.VenueZone;
import com.example.ticketsystem.enums.TicketStatus;
import com.example.ticketsystem.exception.BusinessValidationException;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository repository;
    private final SessionService sessionService;
    private final VenueService venueService;

    public TicketService(TicketRepository repository, SessionService sessionService, VenueService venueService) {
        this.repository = repository;
        this.sessionService = sessionService;
        this.venueService = venueService;
    }

    @Transactional
    public SellTicketResponse sell(SellTicketRequest request) {
        ShowSession session = sessionService.findOrThrow(request.getSessionId());
        validateDuplicateSeatsInRequest(request.getItems());

        long totalSold = repository.countBySessionIdAndStatus(session.getId(), TicketStatus.SOLD);
        if (totalSold + request.getItems().size() > session.getTotalCapacity()) {
            throw new BusinessValidationException("Requested tickets exceed total session capacity");
        }

        Map<Long, Integer> requestedByZone = new HashMap<>();
        List<Ticket> createdTickets = new ArrayList<>();
        String normalizedCustomerName = normalize(request.getCustomerName());
        LocalDateTime soldAt = LocalDateTime.now();

        for (SellTicketItemRequest item : request.getItems()) {
            VenueZone zone = venueService.findZoneOrThrow(item.getZoneId());
            if (!zone.getVenue().getId().equals(session.getVenue().getId())) {
                throw new BusinessValidationException("Zone " + zone.getId() + " does not belong to the venue of the informed session");
            }

            String normalizedSeat = venueService.normalizeSeatNumber(item.getSeatNumber());
            venueService.validateSeatNumber(zone, normalizedSeat);

            if (repository.existsBySessionIdAndZoneIdAndSeatNumberIgnoreCaseAndStatus(session.getId(), zone.getId(), normalizedSeat, TicketStatus.SOLD)) {
                throw new BusinessValidationException("Seat " + normalizedSeat + " is already sold for this session and zone");
            }

            int soldInZone = (int) repository.countBySessionIdAndZoneIdAndStatus(session.getId(), zone.getId(), TicketStatus.SOLD);
            int requestedInZone = requestedByZone.getOrDefault(zone.getId(), 0) + 1;
            if (soldInZone + requestedInZone > zone.getMaxCapacity()) {
                throw new BusinessValidationException("Requested tickets exceed max capacity for zone " + zone.getName());
            }
            requestedByZone.put(zone.getId(), requestedInZone);

            Ticket ticket = new Ticket();
            ticket.setSession(session);
            ticket.setZone(zone);
            ticket.setSeatNumber(normalizedSeat);
            ticket.setCustomerName(normalizedCustomerName);
            ticket.setStatus(TicketStatus.SOLD);
            ticket.setSoldAt(soldAt);
            createdTickets.add(ticket);
        }

        List<TicketResponse> responses = repository.saveAll(createdTickets)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        SellTicketResponse response = new SellTicketResponse();
        response.setSessionId(session.getId());
        response.setCustomerName(normalizedCustomerName);
        response.setTickets(responses);
        response.setTotalTickets(responses.size());
        return response;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list(Long sessionId, Long zoneId, TicketStatus status) {
        return repository.findAllByFilters(sessionId, zoneId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public TicketResponse cancel(Long id) {
        Ticket ticket = findOrThrow(id);
        ticket.setStatus(TicketStatus.CANCELLED);
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for id " + id));
    }

    private void validateDuplicateSeatsInRequest(List<SellTicketItemRequest> items) {
        Set<String> uniqueKeys = new HashSet<>();
        for (SellTicketItemRequest item : items) {
            String key = item.getZoneId() + "::" + item.getSeatNumber().trim().toUpperCase();
            if (!uniqueKeys.add(key)) {
                throw new BusinessValidationException("Duplicated seat in request: " + item.getSeatNumber());
            }
        }
    }

    private TicketResponse toResponse(Ticket entity) {
        TicketResponse response = new TicketResponse();
        response.setTicketId(entity.getId());
        response.setSessionId(entity.getSession().getId());
        response.setShowName(entity.getSession().getShow().getName());
        response.setEventDateTime(entity.getSession().getEventDateTime());
        response.setVenueName(entity.getSession().getVenue().getName());
        response.setZoneId(entity.getZone().getId());
        response.setZoneName(entity.getZone().getName());
        response.setSeatNumber(entity.getSeatNumber());
        response.setCustomerName(entity.getCustomerName());
        response.setPrice(entity.getPrice());
        response.setStatus(entity.getStatus());
        response.setSoldAt(entity.getSoldAt());
        return response;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
