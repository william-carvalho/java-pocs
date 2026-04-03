package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.VenueZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueZoneRepository extends JpaRepository<VenueZone, Long> {

    List<VenueZone> findByVenueIdOrderByIdAsc(Long venueId);

    Optional<VenueZone> findByIdAndVenueId(Long id, Long venueId);
}
