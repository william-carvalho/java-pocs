package com.example.notes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NoteTakingSystem {
    private final Map<Long, Note> notesById = new LinkedHashMap<Long, Note>();
    private final TimeSource timeSource;
    private long nextId = 1;

    public NoteTakingSystem() {
        this(new TimeSource() {
            public LocalDateTime now() {
                return LocalDateTime.now();
            }
        });
    }

    public NoteTakingSystem(TimeSource timeSource) {
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource is required");
        }
        this.timeSource = timeSource;
    }

    public Note addNote(String title, String content) {
        LocalDateTime now = timeSource.now();
        Note note = new Note(nextId++, title, content, now);
        notesById.put(note.getId(), note);
        return note;
    }

    public Note saveNote(long id) {
        Note note = findExistingNote(id);
        note.save(timeSource.now());
        return note;
    }

    public Note editNote(long id, String title, String content) {
        Note note = findActiveNote(id);
        note.edit(title, content, timeSource.now());
        return note;
    }

    public Note deleteNote(long id) {
        Note note = findActiveNote(id);
        note.delete(timeSource.now());
        return note;
    }

    public Note findNote(long id) {
        return findActiveNote(id);
    }

    public List<Note> listNotes() {
        List<Note> notes = new ArrayList<Note>();
        for (Note note : notesById.values()) {
            if (!note.isDeleted()) {
                notes.add(note);
            }
        }
        return Collections.unmodifiableList(notes);
    }

    public List<Note> listAllNotes() {
        return Collections.unmodifiableList(new ArrayList<Note>(notesById.values()));
    }

    public SyncResult sync(LocalDateTime updatedAfter) {
        if (updatedAfter == null) {
            throw new IllegalArgumentException("updatedAfter is required");
        }

        List<Note> changed = new ArrayList<Note>();
        for (Note note : notesById.values()) {
            if (note.getUpdatedAt().isAfter(updatedAfter)) {
                changed.add(note);
            }
        }
        return new SyncResult(updatedAfter, changed);
    }

    public static NoteTakingSystem withDefaultNotes() {
        NoteTakingSystem system = new NoteTakingSystem();
        system.saveNote(system.addNote("Welcome", "This is your first note").getId());
        system.saveNote(system.addNote("Tasks", "Finish POC").getId());
        system.saveNote(system.addNote("Ideas", "Build sync feature").getId());
        return system;
    }

    private Note findActiveNote(long id) {
        Note note = findExistingNote(id);
        if (note.isDeleted()) {
            throw new IllegalArgumentException("Note not found: " + id);
        }
        return note;
    }

    private Note findExistingNote(long id) {
        Note note = notesById.get(id);
        if (note == null) {
            throw new IllegalArgumentException("Note not found: " + id);
        }
        return note;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface TimeSource {
        LocalDateTime now();
    }

    public enum NoteStatus {
        DRAFT,
        SAVED,
        DELETED
    }

    public static final class Note {
        private final long id;
        private String title;
        private String content;
        private NoteStatus status;
        private boolean deleted;
        private int version;
        private final LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime savedAt;
        private LocalDateTime deletedAt;

        private Note(long id, String title, String content, LocalDateTime now) {
            validateText(title, "title");
            validateText(content, "content");
            this.id = id;
            this.title = title.trim();
            this.content = content.trim();
            this.status = NoteStatus.DRAFT;
            this.version = 1;
            this.createdAt = now;
            this.updatedAt = now;
        }

        private void save(LocalDateTime now) {
            if (deleted) {
                throw new IllegalStateException("Deleted notes cannot be saved");
            }
            status = NoteStatus.SAVED;
            savedAt = now;
            updatedAt = now;
        }

        private void edit(String title, String content, LocalDateTime now) {
            validateText(title, "title");
            validateText(content, "content");
            this.title = title.trim();
            this.content = content.trim();
            this.version++;
            this.updatedAt = now;
            if (status == NoteStatus.SAVED) {
                status = NoteStatus.DRAFT;
            }
        }

        private void delete(LocalDateTime now) {
            deleted = true;
            status = NoteStatus.DELETED;
            version++;
            updatedAt = now;
            deletedAt = now;
        }

        private static void validateText(String value, String field) {
            if (isBlank(value)) {
                throw new IllegalArgumentException(field + " is required");
            }
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public NoteStatus getStatus() {
            return status;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public int getVersion() {
            return version;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public LocalDateTime getSavedAt() {
            return savedAt;
        }

        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }
    }

    public static final class SyncResult {
        private final LocalDateTime updatedAfter;
        private final List<Note> notes;

        private SyncResult(LocalDateTime updatedAfter, List<Note> notes) {
            this.updatedAfter = updatedAfter;
            this.notes = Collections.unmodifiableList(new ArrayList<Note>(notes));
        }

        public LocalDateTime getUpdatedAfter() {
            return updatedAfter;
        }

        public int getCount() {
            return notes.size();
        }

        public List<Note> getNotes() {
            return notes;
        }
    }
}
