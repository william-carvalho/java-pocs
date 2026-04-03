package com.example.calendarsystem;

import com.example.calendarsystem.entity.Meeting;
import com.example.calendarsystem.entity.MeetingStatus;
import com.example.calendarsystem.entity.Person;
import com.example.calendarsystem.repository.MeetingRepository;
import com.example.calendarsystem.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CalendarSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @BeforeEach
    void setUp() {
        meetingRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void shouldCreateMeetingListAndCancel() throws Exception {
        Person william = savePerson("William", "william@example.com");
        Person ricardo = savePerson("Ricardo", "ricardo@example.com");

        mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Tech Alignment\",\"description\":\"Weekly sync\",\"organizerId\":" + william.getId() + ",\"participantIds\":[" + ricardo.getId() + "],\"startDateTime\":\"2026-04-03T10:00:00\",\"endDateTime\":\"2026-04-03T11:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Tech Alignment")))
                .andExpect(jsonPath("$.status", is("SCHEDULED")))
                .andExpect(jsonPath("$.participants", hasSize(1)));

        Long meetingId = meetingRepository.findAll().get(0).getId();

        mockMvc.perform(get("/meetings").param("personId", william.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(meetingId.intValue())));

        mockMvc.perform(delete("/meetings/{id}", meetingId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/meetings/{id}", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void shouldRejectConflictingMeeting() throws Exception {
        Person william = savePerson("William", "william@example.com");
        Person ricardo = savePerson("Ricardo", "ricardo@example.com");

        meetingRepository.save(createMeeting(
                "Existing Meeting",
                william,
                new LinkedHashSet<Person>(Arrays.asList(ricardo)),
                LocalDateTime.of(2026, 4, 3, 10, 0),
                LocalDateTime.of(2026, 4, 3, 11, 0)
        ));

        mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Overlap\",\"organizerId\":" + william.getId() + ",\"participantIds\":[" + ricardo.getId() + "],\"startDateTime\":\"2026-04-03T10:30:00\",\"endDateTime\":\"2026-04-03T11:30:00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("One or more people already have a meeting in the selected time range")));
    }

    @Test
    void shouldSuggestBestTimeForTwoPeople() throws Exception {
        Person william = savePerson("William", "william@example.com");
        Person ricardo = savePerson("Ricardo", "ricardo@example.com");

        meetingRepository.save(createMeeting(
                "P1 Slot 1",
                william,
                new LinkedHashSet<Person>(),
                LocalDateTime.of(2026, 4, 3, 9, 0),
                LocalDateTime.of(2026, 4, 3, 10, 0)
        ));
        meetingRepository.save(createMeeting(
                "P1 Slot 2",
                william,
                new LinkedHashSet<Person>(),
                LocalDateTime.of(2026, 4, 3, 13, 0),
                LocalDateTime.of(2026, 4, 3, 14, 0)
        ));
        meetingRepository.save(createMeeting(
                "P2 Slot 1",
                ricardo,
                new LinkedHashSet<Person>(),
                LocalDateTime.of(2026, 4, 3, 10, 0),
                LocalDateTime.of(2026, 4, 3, 11, 0)
        ));
        meetingRepository.save(createMeeting(
                "P2 Slot 2",
                ricardo,
                new LinkedHashSet<Person>(),
                LocalDateTime.of(2026, 4, 3, 15, 0),
                LocalDateTime.of(2026, 4, 3, 16, 0)
        ));

        mockMvc.perform(post("/meetings/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"personOneId\":" + william.getId() + ",\"personTwoId\":" + ricardo.getId() + ",\"searchStart\":\"2026-04-03T09:00:00\",\"searchEnd\":\"2026-04-03T18:00:00\",\"durationMinutes\":60}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedStart", is("2026-04-03T11:00:00")))
                .andExpect(jsonPath("$.suggestedEnd", is("2026-04-03T12:00:00")))
                .andExpect(jsonPath("$.message", is("First available slot found")));
    }

    private Person savePerson(String name, String email) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        return personRepository.save(person);
    }

    private Meeting createMeeting(String title, Person organizer, LinkedHashSet<Person> participants,
                                  LocalDateTime start, LocalDateTime end) {
        Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setOrganizer(organizer);
        meeting.setParticipants(participants);
        meeting.setStartDateTime(start);
        meeting.setEndDateTime(end);
        meeting.setStatus(MeetingStatus.SCHEDULED);
        return meeting;
    }
}

