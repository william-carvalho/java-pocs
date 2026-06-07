package com.example.calendar;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.calendar.CalendarSystem.MeetingStatus.BOOKED;
import static com.example.calendar.CalendarSystem.MeetingStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalendarSystemTest {
    @Test
    void booksMeetingForPeople() {
        CalendarSystem calendar = baseCalendar();

        CalendarSystem.Meeting meeting = calendar.bookMeeting(
                "Planning",
                "Weekly planning",
                LocalDateTime.of(2026, 6, 6, 9, 0),
                LocalDateTime.of(2026, 6, 6, 10, 0),
                "ana@example.com",
                "bob@example.com");

        assertEquals(1L, meeting.getId());
        assertEquals("Planning", meeting.getTitle());
        assertEquals(2, meeting.getParticipants().size());
        assertEquals(BOOKED, meeting.getStatus());
        assertEquals(1, calendar.listMeetings().size());
    }

    @Test
    void blocksOverlappingMeetingForSamePerson() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("Planning", "", dt(9, 0), dt(10, 0), "ana@example.com", "bob@example.com");

        assertThrows(IllegalStateException.class, () ->
                calendar.bookMeeting("Conflict", "", dt(9, 30), dt(10, 30), "ana@example.com", "carla@example.com"));
    }

    @Test
    void allowsBackToBackMeetings() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("First", "", dt(9, 0), dt(10, 0), "ana@example.com");

        CalendarSystem.Meeting second = calendar.bookMeeting("Second", "", dt(10, 0), dt(11, 0), "ana@example.com");

        assertEquals(2L, second.getId());
        assertEquals(2, calendar.listMeetings("ana@example.com").size());
    }

    @Test
    void removeMeetingCancelsItAndRemovesItFromActiveListings() {
        CalendarSystem calendar = baseCalendar();
        CalendarSystem.Meeting meeting = calendar.bookMeeting("Planning", "", dt(9, 0), dt(10, 0), "ana@example.com");

        CalendarSystem.Meeting cancelled = calendar.removeMeeting(meeting.getId());

        assertEquals(CANCELLED, cancelled.getStatus());
        assertEquals(0, calendar.listMeetings().size());
        assertEquals(1, calendar.listAllMeetings().size());
    }

    @Test
    void cancelledMeetingsDoNotBlockNewBookings() {
        CalendarSystem calendar = baseCalendar();
        CalendarSystem.Meeting meeting = calendar.bookMeeting("Planning", "", dt(9, 0), dt(10, 0), "ana@example.com");
        calendar.removeMeeting(meeting.getId());

        CalendarSystem.Meeting replacement = calendar.bookMeeting("Replacement", "", dt(9, 0), dt(10, 0), "ana@example.com");

        assertEquals(BOOKED, replacement.getStatus());
        assertEquals(1, calendar.listMeetings().size());
    }

    @Test
    void listsMeetingsByPersonAndDate() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("Today", "", dt(9, 0), dt(10, 0), "ana@example.com");
        calendar.bookMeeting("Tomorrow", "", LocalDateTime.of(2026, 6, 7, 9, 0), LocalDateTime.of(2026, 6, 7, 10, 0), "ana@example.com");
        calendar.bookMeeting("Other Person", "", dt(11, 0), dt(12, 0), "bob@example.com");

        assertEquals(2, calendar.listMeetings("ana@example.com").size());
        assertEquals(1, calendar.listMeetings("ana@example.com", LocalDate.of(2026, 6, 6)).size());
        assertEquals("Today", calendar.listMeetings("ana@example.com", LocalDate.of(2026, 6, 6)).get(0).getTitle());
    }

    @Test
    void suggestsFirstAvailableTimeForTwoPeople() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("Ana busy", "", dt(9, 0), dt(10, 0), "ana@example.com");
        calendar.bookMeeting("Bob busy", "", dt(10, 0), dt(11, 0), "bob@example.com");

        CalendarSystem.TimeSuggestion suggestion = calendar.suggestBestTime(
                "ana@example.com",
                "bob@example.com",
                dt(9, 0),
                dt(13, 0),
                60);

        assertEquals(dt(11, 0), suggestion.getStart());
        assertEquals(dt(12, 0), suggestion.getEnd());
        assertEquals(60, suggestion.getDurationMinutes());
        assertEquals("First available slot found", suggestion.getMessage());
    }

    @Test
    void suggestionScansInThirtyMinuteSteps() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("Ana busy", "", dt(9, 0), dt(9, 30), "ana@example.com");

        CalendarSystem.TimeSuggestion suggestion = calendar.suggestBestTime(
                "ana@example.com",
                "bob@example.com",
                dt(9, 0),
                dt(10, 30),
                30);

        assertEquals(dt(9, 30), suggestion.getStart());
        assertEquals(dt(10, 0), suggestion.getEnd());
    }

    @Test
    void suggestionFailsWhenThereIsNoSharedSlot() {
        CalendarSystem calendar = baseCalendar();
        calendar.bookMeeting("Ana busy", "", dt(9, 0), dt(10, 0), "ana@example.com");
        calendar.bookMeeting("Bob busy", "", dt(10, 0), dt(11, 0), "bob@example.com");

        assertThrows(IllegalStateException.class, () ->
                calendar.suggestBestTime("ana@example.com", "bob@example.com", dt(9, 0), dt(11, 0), 60));
    }

    @Test
    void createsDefaultData() {
        CalendarSystem calendar = CalendarSystem.withDefaultData();

        assertEquals(3, calendar.listMeetings().size());
        assertEquals(2, calendar.listMeetings("william@example.com", LocalDate.of(2026, 4, 3)).size());
    }

    @Test
    void rejectsInvalidInputs() {
        CalendarSystem calendar = baseCalendar();

        assertThrows(IllegalArgumentException.class, () -> calendar.addPerson("", "x@example.com"));
        assertThrows(IllegalArgumentException.class, () -> calendar.bookMeeting("Bad", "", dt(10, 0), dt(9, 0), "ana@example.com"));
        assertThrows(IllegalArgumentException.class, () -> calendar.bookMeeting("Bad", "", dt(9, 0), dt(10, 0)));
        assertThrows(IllegalArgumentException.class, () -> calendar.suggestBestTime("ana@example.com", "bob@example.com", dt(9, 0), dt(10, 0), 0));
    }

    private static CalendarSystem baseCalendar() {
        CalendarSystem calendar = new CalendarSystem();
        calendar.addPerson("Ana", "ana@example.com");
        calendar.addPerson("Bob", "bob@example.com");
        calendar.addPerson("Carla", "carla@example.com");
        return calendar;
    }

    private static LocalDateTime dt(int hour, int minute) {
        return LocalDateTime.of(2026, 6, 6, hour, minute);
    }
}
