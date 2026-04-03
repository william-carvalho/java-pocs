package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.AvailabilityResponse;
import com.example.ticketsystem.dto.AvailabilityZoneResponse;
import com.example.ticketsystem.entity.ShowSession;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.entity.VenueZone;
import com.example.ticketsystem.enums.TicketStatus;
import com.example.ticketsystem.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AvailabilityService {

    private final SessionService sessionService;
    private final VenueService venueService;
    private final TicketRepository ticketRepository;

    public AvailabilityService(SessionService sessionService, VenueService venueService, TicketRepository ticketRepository) {
        this.sessionService = sessionService;
        this.venueService = venueService;
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long sessionId) {
        ShowSession session = sessionService.findOrThrow(sessionId);
        List<VenueZone> zones = venueService.findZonesByVenue(session.getVenue().getId());
        List<Ticket> soldTickets = ticketRepository.findBySessionIdAndStatusOrderByZoneIdAscSeatNumberAsc(sessionId, TicketStatus.SOLD);

        Map<Long, Set<String>> occupiedSeatsByZone = new HashMap<>();
        for (Ticket ticket : soldTickets) {
            occupiedSeatsByZone
                    .computeIfAbsent(ticket.getZone().getId(), key -> new HashSet<>())
                    .add(ticket.getSeatNumber().toUpperCase());
        }

        List<AvailabilityZoneResponse> zoneResponses = new ArrayList<>();
        for (VenueZone zone : zones) {
            List<String> validSeats = venueService.generateValidSeats(zone);
            Set<String> occupiedSet = occupiedSeatsByZone.getOrDefault(zone.getId(), new HashSet<String>());
            List<String> occupiedSeats = new ArrayList<>();
            List<String> availableSeats = new ArrayList<>();

            for (String seat : validSeats) {
                if (occupiedSet.contains(seat)) {
                    occupiedSeats.add(seat);
                } else {
                    availableSeats.add(seat);
                }
            }

            int soldCount = occupiedSeats.size();
            int availableCount = zone.getMaxCapacity() - soldCount;
            if (availableSeats.size() > availableCount) {
                availableSeats = availableSeats.subList(0, availableCount);
            }

            AvailabilityZoneResponse zoneResponse = new AvailabilityZoneResponse();
            zoneResponse.setZoneId(zone.getId());
            zoneResponse.setZoneName(zone.getName());
            zoneResponse.setMaxCapacity(zone.getMaxCapacity());
            zoneResponse.setSoldCount(soldCount);
            zoneResponse.setAvailableCount(availableCount);
            zoneResponse.setOccupiedSeats(occupiedSeats);
            zoneResponse.setAvailableSeats(availableSeats);
            zoneResponses.add(zoneResponse);
        }

        AvailabilityResponse response = new AvailabilityResponse();
        response.setSessionId(session.getId());
        response.setShowName(session.getShow().getName());
        response.setEventDateTime(session.getEventDateTime());
        response.setZones(zoneResponses);
        return response;
    }
}
