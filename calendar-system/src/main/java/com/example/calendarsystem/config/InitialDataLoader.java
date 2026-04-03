package com.example.calendarsystem.config;

import com.example.calendarsystem.entity.Meeting;
import com.example.calendarsystem.entity.MeetingStatus;
import com.example.calendarsystem.entity.Person;
import com.example.calendarsystem.repository.MeetingRepository;
import com.example.calendarsystem.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadData(PersonRepository personRepository, MeetingRepository meetingRepository) {
        return args -> {
            if (personRepository.count() > 0) {
                return;
            }

            Person william = savePerson(personRepository, "William", "william@example.com");
            Person ricardo = savePerson(personRepository, "Ricardo", "ricardo@example.com");
            Person fabio = savePerson(personRepository, "Fabio", "fabio@example.com");

            meetingRepository.save(createMeeting(
                    "Morning Sync",
                    "William and Ricardo",
                    william,
                    new LinkedHashSet<>(Collections.singletonList(ricardo)),
                    LocalDateTime.of(2026, 4, 3, 9, 0),
                    LocalDateTime.of(2026, 4, 3, 10, 0)
            ));

            meetingRepository.save(createMeeting(
                    "Planning Block",
                    "William focus time",
                    william,
                    new LinkedHashSet<Person>(),
                    LocalDateTime.of(2026, 4, 3, 13, 0),
                    LocalDateTime.of(2026, 4, 3, 14, 0)
            ));

            meetingRepository.save(createMeeting(
                    "Ricardo Review",
                    "Ricardo solo",
                    ricardo,
                    new LinkedHashSet<Person>(),
                    LocalDateTime.of(2026, 4, 3, 10, 0),
                    LocalDateTime.of(2026, 4, 3, 11, 0)
            ));
        };
    }

    private Person savePerson(PersonRepository repository, String name, String email) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        return repository.save(person);
    }

    private Meeting createMeeting(String title, String description, Person organizer, LinkedHashSet<Person> participants,
                                  LocalDateTime start, LocalDateTime end) {
        Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setOrganizer(organizer);
        meeting.setParticipants(participants);
        meeting.setStartDateTime(start);
        meeting.setEndDateTime(end);
        meeting.setStatus(MeetingStatus.SCHEDULED);
        return meeting;
    }
}
