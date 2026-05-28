package com.example.calendarsingleclass;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalendarSingleClassApplicationTest {

    @Test
    void booksAndListsMeetings() {
        CalendarSingleClassApplication app = new CalendarSingleClassApplication();

        CalendarSingleClassApplication.Meeting meeting = app.book(CalendarSingleClassApplication.meeting(
                "Planning",
                "ana",
                "bob",
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));

        assertThat(meeting.id).isEqualTo(1);
        assertThat(app.listMeetings(null, null, false)).hasSize(1);
        assertThat(app.listMeetings("ana", LocalDate.parse("2026-05-28"), false)).hasSize(1);
        assertThat(app.listMeetings("carla", null, false)).isEmpty();
    }

    @Test
    void rejectsOverlappingMeetingsForTheSamePerson() {
        CalendarSingleClassApplication app = new CalendarSingleClassApplication();
        app.book(CalendarSingleClassApplication.meeting(
                "Planning",
                "ana",
                "bob",
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));

        assertThatThrownBy(() -> app.book(CalendarSingleClassApplication.meeting(
                "Conflict",
                "ana",
                "carla",
                "2026-05-28T09:30:00",
                "2026-05-28T10:30:00"
        ))).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already has a meeting");
    }

    @Test
    void removesMeetingsLogically() {
        CalendarSingleClassApplication app = new CalendarSingleClassApplication();
        CalendarSingleClassApplication.Meeting meeting = app.book(CalendarSingleClassApplication.meeting(
                "Planning",
                "ana",
                "bob",
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));

        app.remove(meeting.id);

        assertThat(app.listMeetings(null, null, false)).isEmpty();
        assertThat(app.listMeetings(null, null, true)).hasSize(1);
        assertThat(app.listMeetings(null, null, true).get(0).cancelled).isTrue();
    }

    @Test
    void cancelledMeetingsDoNotBlockNewBookings() {
        CalendarSingleClassApplication app = new CalendarSingleClassApplication();
        CalendarSingleClassApplication.Meeting meeting = app.book(CalendarSingleClassApplication.meeting(
                "Planning",
                "ana",
                "bob",
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));
        app.remove(meeting.id);

        CalendarSingleClassApplication.Meeting replacement = app.book(CalendarSingleClassApplication.meeting(
                "Replacement",
                "ana",
                "bob",
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));

        assertThat(replacement.id).isEqualTo(2);
        assertThat(app.listMeetings(null, null, false)).hasSize(1);
    }

    @Test
    void suggestsBestTimeForTwoPeople() {
        CalendarSingleClassApplication app = new CalendarSingleClassApplication();
        app.book(CalendarSingleClassApplication.meeting(
                "Ana busy",
                "ana",
                null,
                "2026-05-28T09:00:00",
                "2026-05-28T10:00:00"
        ));
        app.book(CalendarSingleClassApplication.meeting(
                "Bob busy",
                "bob",
                null,
                "2026-05-28T10:00:00",
                "2026-05-28T11:00:00"
        ));

        CalendarSingleClassApplication.SuggestRequest request = new CalendarSingleClassApplication.SuggestRequest();
        request.people = Arrays.asList("ana", "bob");
        request.searchStart = LocalDateTime.parse("2026-05-28T09:00:00");
        request.searchEnd = LocalDateTime.parse("2026-05-28T12:00:00");
        request.durationMinutes = 60;

        CalendarSingleClassApplication.Suggestion suggestion = app.suggestBestTime(request);

        assertThat(suggestion.start).isEqualTo(LocalDateTime.parse("2026-05-28T11:00:00"));
        assertThat(suggestion.end).isEqualTo(LocalDateTime.parse("2026-05-28T12:00:00"));
        assertThat(suggestion.people).containsExactly("ana", "bob");
    }
}
