package com.example.notes;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.notes.NoteTakingSystem.NoteStatus.DELETED;
import static com.example.notes.NoteTakingSystem.NoteStatus.DRAFT;
import static com.example.notes.NoteTakingSystem.NoteStatus.SAVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteTakingSystemTest {
    @Test
    void addsNoteAsDraftWithVersionOne() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);

        NoteTakingSystem.Note note = system.addNote("Shopping", "Milk and bread");

        assertEquals(1L, note.getId());
        assertEquals("Shopping", note.getTitle());
        assertEquals("Milk and bread", note.getContent());
        assertEquals(DRAFT, note.getStatus());
        assertEquals(1, note.getVersion());
        assertEquals(clock.now(), note.getCreatedAt());
        assertEquals(clock.now(), note.getUpdatedAt());
    }

    @Test
    void savesNote() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);
        NoteTakingSystem.Note note = system.addNote("Tasks", "Finish tests");
        clock.advanceMinutes(5);

        NoteTakingSystem.Note saved = system.saveNote(note.getId());

        assertEquals(SAVED, saved.getStatus());
        assertEquals(LocalDateTime.of(2026, 6, 5, 10, 5), saved.getSavedAt());
        assertEquals(LocalDateTime.of(2026, 6, 5, 10, 5), saved.getUpdatedAt());
        assertEquals(1, saved.getVersion());
    }

    @Test
    void editsNoteAndIncrementsVersion() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);
        NoteTakingSystem.Note note = system.addNote("Draft", "Old");
        system.saveNote(note.getId());
        clock.advanceMinutes(10);

        NoteTakingSystem.Note edited = system.editNote(note.getId(), "Draft Updated", "New");

        assertEquals("Draft Updated", edited.getTitle());
        assertEquals("New", edited.getContent());
        assertEquals(2, edited.getVersion());
        assertEquals(DRAFT, edited.getStatus());
        assertEquals(LocalDateTime.of(2026, 6, 5, 10, 10), edited.getUpdatedAt());
    }

    @Test
    void deletesNoteLogicallyAndHidesItFromActiveList() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);
        NoteTakingSystem.Note first = system.addNote("First", "Content");
        NoteTakingSystem.Note second = system.addNote("Second", "Content");
        clock.advanceMinutes(1);

        NoteTakingSystem.Note deleted = system.deleteNote(first.getId());

        assertTrue(deleted.isDeleted());
        assertEquals(DELETED, deleted.getStatus());
        assertEquals(2, deleted.getVersion());
        assertNotNull(deleted.getDeletedAt());
        assertEquals(1, system.listNotes().size());
        assertEquals(second.getId(), system.listNotes().get(0).getId());
        assertEquals(2, system.listAllNotes().size());
    }

    @Test
    void syncReturnsNotesUpdatedAfterTimestampIncludingDeletedNotes() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);
        NoteTakingSystem.Note old = system.addNote("Old", "Before sync");
        system.saveNote(old.getId());
        LocalDateTime checkpoint = clock.now();
        clock.advanceMinutes(5);
        NoteTakingSystem.Note edited = system.addNote("Edited", "Before edit");
        system.editNote(edited.getId(), "Edited", "After edit");
        clock.advanceMinutes(5);
        NoteTakingSystem.Note deleted = system.addNote("Delete me", "Temporary");
        system.deleteNote(deleted.getId());

        NoteTakingSystem.SyncResult result = system.sync(checkpoint);

        assertEquals(checkpoint, result.getUpdatedAfter());
        assertEquals(2, result.getCount());
        assertEquals(edited.getId(), result.getNotes().get(0).getId());
        assertEquals(deleted.getId(), result.getNotes().get(1).getId());
        assertTrue(result.getNotes().get(1).isDeleted());
    }

    @Test
    void syncExcludesNotesUpdatedExactlyAtCheckpoint() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0));
        NoteTakingSystem system = new NoteTakingSystem(clock);
        system.addNote("Exact", "Same timestamp");

        NoteTakingSystem.SyncResult result = system.sync(clock.now());

        assertEquals(0, result.getCount());
    }

    @Test
    void activeListKeepsInsertionOrder() {
        NoteTakingSystem system = new NoteTakingSystem(new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0)));
        NoteTakingSystem.Note first = system.addNote("First", "1");
        NoteTakingSystem.Note second = system.addNote("Second", "2");

        List<NoteTakingSystem.Note> notes = system.listNotes();

        assertEquals(first.getId(), notes.get(0).getId());
        assertEquals(second.getId(), notes.get(1).getId());
    }

    @Test
    void rejectsBlankTitleAndContent() {
        NoteTakingSystem system = new NoteTakingSystem(new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0)));

        assertThrows(IllegalArgumentException.class, () -> system.addNote(" ", "Content"));
        assertThrows(IllegalArgumentException.class, () -> system.addNote("Title", ""));
    }

    @Test
    void rejectsOperationsForMissingOrDeletedNotes() {
        NoteTakingSystem system = new NoteTakingSystem(new MutableClock(LocalDateTime.of(2026, 6, 5, 10, 0)));
        NoteTakingSystem.Note note = system.addNote("Delete", "Me");
        system.deleteNote(note.getId());

        assertThrows(IllegalArgumentException.class, () -> system.findNote(99));
        assertThrows(IllegalArgumentException.class, () -> system.editNote(note.getId(), "No", "No"));
        assertThrows(IllegalArgumentException.class, () -> system.findNote(note.getId()));
        assertThrows(IllegalStateException.class, () -> system.saveNote(note.getId()));
    }

    @Test
    void createsDefaultSavedNotes() {
        NoteTakingSystem system = NoteTakingSystem.withDefaultNotes();

        assertEquals(3, system.listNotes().size());
        assertFalse(system.listNotes().get(0).isDeleted());
        assertEquals(SAVED, system.listNotes().get(0).getStatus());
    }

    private static final class MutableClock implements NoteTakingSystem.TimeSource {
        private LocalDateTime now;

        private MutableClock(LocalDateTime now) {
            this.now = now;
        }

        public LocalDateTime now() {
            return now;
        }

        private void advanceMinutes(long minutes) {
            now = now.plusMinutes(minutes);
        }
    }
}
