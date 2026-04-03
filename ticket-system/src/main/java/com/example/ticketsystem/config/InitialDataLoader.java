package com.example.ticketsystem.config;

import com.example.ticketsystem.entity.ShowEvent;
import com.example.ticketsystem.entity.ShowSession;
import com.example.ticketsystem.entity.Venue;
import com.example.ticketsystem.entity.VenueZone;
import com.example.ticketsystem.repository.ShowEventRepository;
import com.example.ticketsystem.repository.ShowSessionRepository;
import com.example.ticketsystem.repository.VenueRepository;
import com.example.ticketsystem.repository.VenueZoneRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class InitialDataLoader {

    @Bean
    public CommandLineRunner loadInitialData(ShowEventRepository showRepository,
                                             VenueRepository venueRepository,
                                             VenueZoneRepository zoneRepository,
                                             ShowSessionRepository sessionRepository) {
        return args -> {
            if (showRepository.count() == 0) {
                showRepository.saveAll(Arrays.asList(
                        buildShow("Rock Night", null),
                        buildShow("Jazz Festival", null),
                        buildShow("Pop Experience", null)
                ));
            }

            if (venueRepository.count() == 0) {
                venueRepository.saveAll(Arrays.asList(
                        buildVenue("Arena Floripa", "Florianopolis"),
                        buildVenue("Teatro Central", "Belo Horizonte")
                ));
            }

            if (zoneRepository.count() == 0) {
                Venue arena = venueRepository.findAll().stream().filter(v -> "Arena Floripa".equals(v.getName())).findFirst().get();
                Venue teatro = venueRepository.findAll().stream().filter(v -> "Teatro Central".equals(v.getName())).findFirst().get();

                zoneRepository.saveAll(Arrays.asList(
                        buildZone(arena, "VIP", 50, "A", 1, 50),
                        buildZone(arena, "PREMIUM", 100, "B", 1, 100),
                        buildZone(arena, "STANDARD", 300, "C", 1, 300),
                        buildZone(teatro, "PLATEA", 80, "D", 1, 80),
                        buildZone(teatro, "BALCONY", 120, "E", 1, 120)
                ));
            }

            if (sessionRepository.count() == 0) {
                List<ShowEvent> shows = showRepository.findAll();
                List<Venue> venues = venueRepository.findAll();
                ShowEvent rockNight = shows.stream().filter(s -> "Rock Night".equals(s.getName())).findFirst().get();
                ShowEvent jazzFestival = shows.stream().filter(s -> "Jazz Festival".equals(s.getName())).findFirst().get();
                Venue arena = venues.stream().filter(v -> "Arena Floripa".equals(v.getName())).findFirst().get();
                Venue teatro = venues.stream().filter(v -> "Teatro Central".equals(v.getName())).findFirst().get();

                sessionRepository.saveAll(Arrays.asList(
                        buildSession(rockNight, arena, LocalDateTime.of(2026, 4, 20, 20, 0), 450),
                        buildSession(jazzFestival, teatro, LocalDateTime.of(2026, 4, 25, 21, 0), 200)
                ));
            }
        };
    }

    private ShowEvent buildShow(String name, String description) {
        ShowEvent entity = new ShowEvent();
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }

    private Venue buildVenue(String name, String city) {
        Venue entity = new Venue();
        entity.setName(name);
        entity.setCity(city);
        return entity;
    }

    private VenueZone buildZone(Venue venue, String name, int maxCapacity, String prefix, int seatStart, int seatEnd) {
        VenueZone entity = new VenueZone();
        entity.setVenue(venue);
        entity.setName(name);
        entity.setMaxCapacity(maxCapacity);
        entity.setSeatPrefix(prefix);
        entity.setSeatStart(seatStart);
        entity.setSeatEnd(seatEnd);
        return entity;
    }

    private ShowSession buildSession(ShowEvent show, Venue venue, LocalDateTime eventDateTime, int totalCapacity) {
        ShowSession entity = new ShowSession();
        entity.setShow(show);
        entity.setVenue(venue);
        entity.setEventDateTime(eventDateTime);
        entity.setTotalCapacity(totalCapacity);
        return entity;
    }
}
