package com.example.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CalendarSystem {
    private final Map<String, Person> peopleByEmail = new LinkedHashMap<String, Person>();
    private final List<Meeting> meetings = new ArrayList<Meeting>();
    private long nextPersonId = 1;
    private long nextMeetingId = 1;

    public Person addPerson(String name, String email) {
        Person person = new Person(nextPersonId++, name, email);
        if (peopleByEmail.containsKey(person.getEmail())) {
            throw new IllegalStateException("Person already exists: " + person.getEmail());
        }
        peopleByEmail.put(person.getEmail(), person);
        return person;
    }

    public Meeting bookMeeting(String title, String description, LocalDateTime start, LocalDateTime end, String... participantEmails) {
        validateTimeRange(start, end);
        if (participantEmails == null || participantEmails.length == 0) {
            throw new IllegalArgumentException("at least one participant is required");
        }

        List<Person> participants = resolveParticipants(participantEmails);
        validateNoConflicts(participants, start, end);

        Meeting meeting = new Meeting(nextMeetingId++, title, description, start, end, participants);
        meetings.add(meeting);
        return meeting;
    }

    public Meeting removeMeeting(long meetingId) {
        Meeting meeting = findMeeting(meetingId);
        meeting.cancel();
        return meeting;
    }

    public List<Meeting> listMeetings() {
        return activeMeetings(null, null);
    }

    public List<Meeting> listMeetings(String participantEmail) {
        return activeMeetings(participantEmail, null);
    }

    public List<Meeting> listMeetings(String participantEmail, LocalDate date) {
        return activeMeetings(participantEmail, date);
    }

    public List<Meeting> listAllMeetings() {
        return Collections.unmodifiableList(new ArrayList<Meeting>(meetings));
    }

    public TimeSuggestion suggestBestTime(String firstPersonEmail,
                                          String secondPersonEmail,
                                          LocalDateTime searchStart,
                                          LocalDateTime searchEnd,
                                          int durationMinutes) {
        Person first = findPerson(firstPersonEmail);
        Person second = findPerson(secondPersonEmail);
        validateTimeRange(searchStart, searchEnd);
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than zero");
        }

        LocalDateTime candidate = searchStart;
        while (!candidate.plusMinutes(durationMinutes).isAfter(searchEnd)) {
            LocalDateTime candidateEnd = candidate.plusMinutes(durationMinutes);
            if (isAvailable(first, candidate, candidateEnd) && isAvailable(second, candidate, candidateEnd)) {
                return new TimeSuggestion(first, second, candidate, candidateEnd, durationMinutes, "First available slot found");
            }
            candidate = candidate.plusMinutes(30);
        }

        throw new IllegalStateException("No available slot found");
    }

    public static CalendarSystem withDefaultData() {
        CalendarSystem calendar = new CalendarSystem();
        calendar.addPerson("William", "william@example.com");
        calendar.addPerson("Ricardo", "ricardo@example.com");
        calendar.addPerson("Fabio", "fabio@example.com");
        calendar.bookMeeting(
                "Morning Sync",
                "William and Ricardo",
                LocalDateTime.of(2026, 4, 3, 9, 0),
                LocalDateTime.of(2026, 4, 3, 10, 0),
                "william@example.com",
                "ricardo@example.com");
        calendar.bookMeeting(
                "Focus Time",
                "William solo block",
                LocalDateTime.of(2026, 4, 3, 13, 0),
                LocalDateTime.of(2026, 4, 3, 14, 0),
                "william@example.com");
        calendar.bookMeeting(
                "Review",
                "Ricardo solo block",
                LocalDateTime.of(2026, 4, 3, 10, 0),
                LocalDateTime.of(2026, 4, 3, 11, 0),
                "ricardo@example.com");
        return calendar;
    }

    private List<Person> resolveParticipants(String[] participantEmails) {
        Set<Person> unique = new LinkedHashSet<Person>();
        for (String email : participantEmails) {
            unique.add(findPerson(email));
        }
        return new ArrayList<Person>(unique);
    }

    private void validateNoConflicts(List<Person> participants, LocalDateTime start, LocalDateTime end) {
        for (Meeting meeting : listMeetings()) {
            if (!overlaps(start, end, meeting.getStart(), meeting.getEnd())) {
                continue;
            }
            for (Person participant : participants) {
                if (meeting.hasParticipant(participant)) {
                    throw new IllegalStateException("Participant has a conflicting meeting: " + participant.getEmail());
                }
            }
        }
    }

    private boolean isAvailable(Person person, LocalDateTime start, LocalDateTime end) {
        for (Meeting meeting : listMeetings(person.getEmail())) {
            if (overlaps(start, end, meeting.getStart(), meeting.getEnd())) {
                return false;
            }
        }
        return true;
    }

    private List<Meeting> activeMeetings(String participantEmail, LocalDate date) {
        Person participant = participantEmail == null ? null : findPerson(participantEmail);
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting : meetings) {
            if (meeting.getStatus() != MeetingStatus.BOOKED) {
                continue;
            }
            if (participant != null && !meeting.hasParticipant(participant)) {
                continue;
            }
            if (date != null && !meeting.getStart().toLocalDate().equals(date)) {
                continue;
            }
            result.add(meeting);
        }
        return Collections.unmodifiableList(result);
    }

    private Person findPerson(String email) {
        if (isBlank(email)) {
            throw new IllegalArgumentException("email is required");
        }
        Person person = peopleByEmail.get(email.trim().toLowerCase());
        if (person == null) {
            throw new IllegalArgumentException("Person not found: " + email);
        }
        return person;
    }

    private Meeting findMeeting(long meetingId) {
        for (Meeting meeting : meetings) {
            if (meeting.getId() == meetingId) {
                return meeting;
            }
        }
        throw new IllegalArgumentException("Meeting not found: " + meetingId);
    }

    private static void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }
    }

    private static boolean overlaps(LocalDateTime start, LocalDateTime end, LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public enum MeetingStatus {
        BOOKED,
        CANCELLED
    }

    public static final class Person {
        private final long id;
        private final String name;
        private final String email;

        private Person(long id, String name, String email) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("name is required");
            }
            if (isBlank(email)) {
                throw new IllegalArgumentException("email is required");
            }
            this.id = id;
            this.name = name.trim();
            this.email = email.trim().toLowerCase();
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    public static final class Meeting {
        private final long id;
        private final String title;
        private final String description;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final List<Person> participants;
        private MeetingStatus status = MeetingStatus.BOOKED;

        private Meeting(long id, String title, String description, LocalDateTime start, LocalDateTime end, List<Person> participants) {
            if (isBlank(title)) {
                throw new IllegalArgumentException("title is required");
            }
            this.id = id;
            this.title = title.trim();
            this.description = description == null ? "" : description.trim();
            this.start = start;
            this.end = end;
            this.participants = Collections.unmodifiableList(new ArrayList<Person>(participants));
        }

        private void cancel() {
            status = MeetingStatus.CANCELLED;
        }

        private boolean hasParticipant(Person person) {
            return participants.contains(person);
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public List<Person> getParticipants() {
            return participants;
        }

        public MeetingStatus getStatus() {
            return status;
        }
    }

    public static final class TimeSuggestion {
        private final Person firstPerson;
        private final Person secondPerson;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final int durationMinutes;
        private final String message;

        private TimeSuggestion(Person firstPerson, Person secondPerson, LocalDateTime start, LocalDateTime end, int durationMinutes, String message) {
            this.firstPerson = firstPerson;
            this.secondPerson = secondPerson;
            this.start = start;
            this.end = end;
            this.durationMinutes = durationMinutes;
            this.message = message;
        }

        public Person getFirstPerson() {
            return firstPerson;
        }

        public Person getSecondPerson() {
            return secondPerson;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public String getMessage() {
            return message;
        }
    }
}
