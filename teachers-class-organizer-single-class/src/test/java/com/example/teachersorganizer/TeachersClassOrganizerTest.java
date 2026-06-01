package com.example.teachersorganizer;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeachersClassOrganizerTest {
    @Test
    void schedulesClassSessionWithTeacherClassSubjectRoomAndTime() {
        TeachersClassOrganizer organizer = baseOrganizer();

        TeachersClassOrganizer.ClassSession session = organizer.schedule(
                "Maria", "Class A", "Math", "Room 101",
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertEquals("Maria", session.getTeacher().getName());
        assertEquals("Class A", session.getSchoolClass().getName());
        assertEquals("Math", session.getSubject().getName());
        assertEquals("Room 101", session.getRoom().getName());
        assertEquals(TeachersClassOrganizer.SessionStatus.SCHEDULED, session.getStatus());
    }

    @Test
    void blocksTeacherConflict() {
        TeachersClassOrganizer organizer = baseOrganizer();
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertThrows(IllegalStateException.class, () ->
                organizer.schedule("Maria", "Class B", "Science", "Room 102", DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
    }

    @Test
    void blocksClassConflict() {
        TeachersClassOrganizer organizer = baseOrganizer();
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertThrows(IllegalStateException.class, () ->
                organizer.schedule("Joao", "Class A", "Science", "Room 102", DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
    }

    @Test
    void blocksRoomConflict() {
        TeachersClassOrganizer organizer = baseOrganizer();
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertThrows(IllegalStateException.class, () ->
                organizer.schedule("Joao", "Class B", "Science", "Room 101", DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
    }

    @Test
    void ignoresCancelledSessionsWhenCheckingConflicts() {
        TeachersClassOrganizer organizer = baseOrganizer();
        TeachersClassOrganizer.ClassSession cancelled = organizer.schedule(
                "Maria", "Class A", "Math", "Room 101",
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));
        organizer.cancelSession(cancelled.getId());

        TeachersClassOrganizer.ClassSession replacement = organizer.schedule(
                "Maria", "Class A", "Math", "Room 101",
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertEquals(TeachersClassOrganizer.SessionStatus.CANCELLED, cancelled.getStatus());
        assertEquals(TeachersClassOrganizer.SessionStatus.SCHEDULED, replacement.getStatus());
        assertEquals(1, organizer.activeSessions().size());
    }

    @Test
    void rejectsRoomWithoutEnoughCapacityForClass() {
        TeachersClassOrganizer organizer = baseOrganizer();

        assertThrows(IllegalStateException.class, () ->
                organizer.schedule("Maria", "Class B", "Math", "Room 101", DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(9, 0)));
    }

    @Test
    void suggestsSlotAdjacentToTeacherScheduleWhenAvailable() {
        TeachersClassOrganizer organizer = TeachersClassOrganizer.withDefaultData();

        TeachersClassOrganizer.SlotSuggestion suggestion = organizer.suggest(
                "Maria", "Class A", "Math", "Room 101",
                60,
                Arrays.asList(DayOfWeek.MONDAY),
                LocalTime.of(8, 0),
                LocalTime.of(12, 0));

        assertEquals(DayOfWeek.MONDAY, suggestion.getDay());
        assertEquals(LocalTime.of(9, 0), suggestion.getStart());
        assertEquals(LocalTime.of(10, 0), suggestion.getEnd());
        assertEquals("Optimized slot adjacent to teacher schedule", suggestion.getMessage());
    }

    @Test
    void suggestionScansInThirtyMinuteBlocks() {
        TeachersClassOrganizer organizer = baseOrganizer();
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(8, 30));

        TeachersClassOrganizer.SlotSuggestion suggestion = organizer.suggest(
                "Maria", "Class A", "Science", "Room 101",
                30,
                Arrays.asList(DayOfWeek.TUESDAY),
                LocalTime.of(8, 0),
                LocalTime.of(9, 0));

        assertEquals(LocalTime.of(8, 30), suggestion.getStart());
        assertEquals(LocalTime.of(9, 0), suggestion.getEnd());
    }

    @Test
    void suggestionFailsWhenNoSlotIsAvailable() {
        TeachersClassOrganizer organizer = baseOrganizer();
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertThrows(IllegalStateException.class, () ->
                organizer.suggest("Maria", "Class A", "Science", "Room 101", 60,
                        Arrays.asList(DayOfWeek.FRIDAY), LocalTime.of(8, 0), LocalTime.of(9, 0)));
    }

    @Test
    void rejectsInvalidDurationForSuggestion() {
        TeachersClassOrganizer organizer = baseOrganizer();

        assertThrows(IllegalArgumentException.class, () ->
                organizer.suggest("Maria", "Class A", "Math", "Room 101", 45,
                        Arrays.asList(DayOfWeek.MONDAY), LocalTime.of(8, 0), LocalTime.of(10, 0)));
    }

    private static TeachersClassOrganizer baseOrganizer() {
        TeachersClassOrganizer organizer = new TeachersClassOrganizer();
        organizer.addTeacher("Maria", "Math");
        organizer.addTeacher("Joao", "Science");
        organizer.addClass("Class A", "5th Grade", 28);
        organizer.addClass("Class B", "6th Grade", 35);
        organizer.addSubject("Math");
        organizer.addSubject("Science");
        organizer.addRoom("Room 101", 30);
        organizer.addRoom("Room 102", 40);
        return organizer;
    }
}
