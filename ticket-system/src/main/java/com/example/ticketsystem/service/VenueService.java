package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.VenueRequest;
import com.example.ticketsystem.dto.VenueResponse;
import com.example.ticketsystem.dto.VenueZoneRequest;
import com.example.ticketsystem.dto.VenueZoneResponse;
import com.example.ticketsystem.entity.Venue;
import com.example.ticketsystem.entity.VenueZone;
import com.example.ticketsystem.exception.BusinessValidationException;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.repository.VenueRepository;
import com.example.ticketsystem.repository.VenueZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueZoneRepository zoneRepository;

    public VenueService(VenueRepository venueRepository, VenueZoneRepository zoneRepository) {
        this.venueRepository = venueRepository;
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        String name = request.getName().trim();
        if (venueRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessValidationException("Venue with this name already exists");
        }

        Venue entity = new Venue();
        entity.setName(name);
        entity.setCity(normalize(request.getCity()));
        return toVenueResponse(venueRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> listVenues() {
        return venueRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Venue::getId))
                .map(this::toVenueResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VenueZoneResponse createZone(Long venueId, VenueZoneRequest request) {
        Venue venue = findVenueOrThrow(venueId);
        validateZoneRequest(request);

        VenueZone entity = new VenueZone();
        entity.setVenue(venue);
        entity.setName(request.getName().trim());
        entity.setMaxCapacity(request.getMaxCapacity());
        entity.setSeatPrefix(normalizeSeatPrefix(request.getSeatPrefix()));
        entity.setSeatStart(request.getSeatStart());
        entity.setSeatEnd(request.getSeatEnd());
        return toZoneResponse(zoneRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<VenueZoneResponse> listZones(Long venueId) {
        findVenueOrThrow(venueId);
        return zoneRepository.findByVenueIdOrderByIdAsc(venueId)
                .stream()
                .map(this::toZoneResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Venue findVenueOrThrow(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found for id " + id));
    }

    @Transactional(readOnly = true)
    public VenueZone findZoneOrThrow(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found for id " + id));
    }

    @Transactional(readOnly = true)
    public List<VenueZone> findZonesByVenue(Long venueId) {
        return zoneRepository.findByVenueIdOrderByIdAsc(venueId);
    }

    @Transactional(readOnly = true)
    public int calculateVenueCapacity(Long venueId) {
        return zoneRepository.findByVenueIdOrderByIdAsc(venueId)
                .stream()
                .mapToInt(VenueZone::getMaxCapacity)
                .sum();
    }

    public void validateSeatNumber(VenueZone zone, String rawSeatNumber) {
        String seatNumber = normalizeSeatNumber(rawSeatNumber);
        String prefix = zone.getSeatPrefix();

        if (prefix != null) {
            if (!seatNumber.startsWith(prefix)) {
                throw new BusinessValidationException("Seat number " + seatNumber + " does not belong to zone " + zone.getName());
            }
            String numericPart = seatNumber.substring(prefix.length());
            validateSeatNumberRange(zone, numericPart, seatNumber);
            return;
        }

        validateSeatNumberRange(zone, seatNumber, seatNumber);
    }

    public List<String> generateValidSeats(VenueZone zone) {
        List<String> seats = new ArrayList<>();
        String prefix = zone.getSeatPrefix() == null ? "" : zone.getSeatPrefix();
        for (int i = zone.getSeatStart(); i <= zone.getSeatEnd(); i++) {
            seats.add(prefix + i);
        }
        return seats;
    }

    public String normalizeSeatNumber(String seatNumber) {
        return seatNumber.trim().toUpperCase();
    }

    private void validateZoneRequest(VenueZoneRequest request) {
        if (request.getSeatStart() > request.getSeatEnd()) {
            throw new BusinessValidationException("seatStart must be less than or equal to seatEnd");
        }

        int possibleSeats = request.getSeatEnd() - request.getSeatStart() + 1;
        if (request.getMaxCapacity() > possibleSeats) {
            throw new BusinessValidationException("maxCapacity cannot be greater than the number of seats in the informed range");
        }
    }

    private void validateSeatNumberRange(VenueZone zone, String numericPart, String seatNumber) {
        int number;
        try {
            number = Integer.parseInt(numericPart);
        } catch (NumberFormatException ex) {
            throw new BusinessValidationException("Seat number " + seatNumber + " is invalid for zone " + zone.getName());
        }

        if (number < zone.getSeatStart() || number > zone.getSeatEnd()) {
            throw new BusinessValidationException("Seat number " + seatNumber + " is outside the allowed range for zone " + zone.getName());
        }
    }

    private VenueResponse toVenueResponse(Venue entity) {
        VenueResponse response = new VenueResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCity(entity.getCity());
        response.setTotalCapacity(calculateVenueCapacity(entity.getId()));
        return response;
    }

    private VenueZoneResponse toZoneResponse(VenueZone entity) {
        VenueZoneResponse response = new VenueZoneResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setMaxCapacity(entity.getMaxCapacity());
        response.setSeatPrefix(entity.getSeatPrefix());
        response.setSeatStart(entity.getSeatStart());
        response.setSeatEnd(entity.getSeatEnd());
        return response;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeSeatPrefix(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
