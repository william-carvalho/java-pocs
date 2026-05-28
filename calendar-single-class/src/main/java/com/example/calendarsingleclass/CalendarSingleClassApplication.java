package com.example.calendarsingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
public class CalendarSingleClassApplication {

    private final AtomicLong ids = new AtomicLong();
    private final Map<Long, Meeting> meetings = new LinkedHashMap<Long, Meeting>();

    public static void main(String[] args) {
        SpringApplication.run(CalendarSingleClassApplication.class, args);
    }

    @PostMapping("/meetings")
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized Meeting book(@RequestBody MeetingRequest request) {
        validate(request);

        Meeting meeting = new Meeting(
                ids.incrementAndGet(),
                request.title,
                request.people,
                request.start,
                request.end,
                false
        );

        for (String person : meeting.people) {
            if (hasConflict(person, meeting.start, meeting.end, 0)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, person + " already has a meeting");
            }
        }

        meetings.put(meeting.id, meeting);
        return meeting;
    }

    @DeleteMapping("/meetings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public synchronized void remove(@PathVariable long id) {
        Meeting meeting = find(id);
        meeting.cancelled = true;
    }

    @GetMapping("/meetings")
    public synchronized List<Meeting> listMeetings(
            @RequestParam(required = false) String person,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean includeCancelled) {
        List<Meeting> result = new ArrayList<Meeting>();

        for (Meeting meeting : meetings.values()) {
            boolean matchesPerson = person == null || meeting.people.contains(person);
            boolean matchesDate = date == null || meeting.start.toLocalDate().equals(date);
            boolean matchesCancelled = includeCancelled || !meeting.cancelled;

            if (matchesPerson && matchesDate && matchesCancelled) {
                result.add(meeting);
            }
        }

        result.sort(Comparator.comparing(Meeting::getStart));
        return result;
    }

    @PostMapping("/meetings/suggest")
    public synchronized Suggestion suggestBestTime(@RequestBody SuggestRequest request) {
        if (request == null || request.people == null || request.people.size() != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exactly two people are required");
        }
        if (request.durationMinutes <= 0 || request.searchStart == null || request.searchEnd == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "search window and duration are required");
        }

        LocalDateTime candidate = request.searchStart;
        while (!candidate.plusMinutes(request.durationMinutes).isAfter(request.searchEnd)) {
            LocalDateTime candidateEnd = candidate.plusMinutes(request.durationMinutes);
            boolean free = true;

            for (String person : request.people) {
                if (hasConflict(person, candidate, candidateEnd, 0)) {
                    free = false;
                    break;
                }
            }

            if (free) {
                return new Suggestion(request.people, candidate, candidateEnd, "First available slot found");
            }

            candidate = candidate.plusMinutes(15);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available slot found");
    }

    private Meeting find(long id) {
        Meeting meeting = meetings.get(id);
        if (meeting == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found: " + id);
        }
        return meeting;
    }

    private void validate(MeetingRequest request) {
        if (request == null || blank(request.title) || request.people == null || request.people.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title and people are required");
        }
        if (request.start == null || request.end == null || !request.start.isBefore(request.end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before end");
        }
    }

    private boolean hasConflict(String person, LocalDateTime start, LocalDateTime end, long ignoredMeetingId) {
        for (Meeting meeting : meetings.values()) {
            if (meeting.id != ignoredMeetingId
                    && !meeting.cancelled
                    && meeting.people.contains(person)
                    && overlaps(start, end, meeting.start, meeting.end)) {
                return true;
            }
        }
        return false;
    }

    private boolean overlaps(LocalDateTime start, LocalDateTime end, LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class MeetingRequest {
        public String title;
        public List<String> people;
        public LocalDateTime start;
        public LocalDateTime end;
    }

    public static class SuggestRequest {
        public List<String> people;
        public LocalDateTime searchStart;
        public LocalDateTime searchEnd;
        public int durationMinutes;
    }

    public static class Meeting {
        public long id;
        public String title;
        public List<String> people;
        public LocalDateTime start;
        public LocalDateTime end;
        public boolean cancelled;

        public Meeting() {
        }

        public Meeting(long id,
                       String title,
                       List<String> people,
                       LocalDateTime start,
                       LocalDateTime end,
                       boolean cancelled) {
            this.id = id;
            this.title = title;
            this.people = new ArrayList<String>(people);
            this.start = start;
            this.end = end;
            this.cancelled = cancelled;
        }

        public LocalDateTime getStart() {
            return start;
        }
    }

    public static class Suggestion {
        public List<String> people;
        public LocalDateTime start;
        public LocalDateTime end;
        public String message;

        public Suggestion(List<String> people, LocalDateTime start, LocalDateTime end, String message) {
            this.people = new ArrayList<String>(people);
            this.start = start;
            this.end = end;
            this.message = message;
        }
    }

    public static MeetingRequest meeting(String title, String personOne, String personTwo, String start, String end) {
        MeetingRequest request = new MeetingRequest();
        request.title = title;
        request.people = personTwo == null
                ? Collections.singletonList(personOne)
                : Arrays.asList(personOne, personTwo);
        request.start = LocalDateTime.parse(start);
        request.end = LocalDateTime.parse(end);
        return request;
    }
}
