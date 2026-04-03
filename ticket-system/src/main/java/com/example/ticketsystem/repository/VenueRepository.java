package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    boolean existsByNameIgnoreCase(String name);
}
