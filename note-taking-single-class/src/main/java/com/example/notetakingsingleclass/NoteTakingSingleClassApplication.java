package com.example.notetakingsingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
public class NoteTakingSingleClassApplication {

    private final AtomicLong ids = new AtomicLong();
    private final Map<Long, Note> notes = new LinkedHashMap<Long, Note>();

    public static void main(String[] args) {
        SpringApplication.run(NoteTakingSingleClassApplication.class, args);
    }

    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized Note add(@RequestBody NoteRequest request) {
        validate(request);

        LocalDateTime now = LocalDateTime.now();
        Note note = new Note(ids.incrementAndGet(), request.title, request.content, false, 1, now, now);
        notes.put(note.id, note);
        return note;
    }

    @PostMapping("/notes/{id}/save")
    public synchronized Note save(@PathVariable long id) {
        Note note = find(id);
        note.updatedAt = LocalDateTime.now();
        return note;
    }

    @PutMapping("/notes/{id}")
    public synchronized Note edit(@PathVariable long id, @RequestBody NoteRequest request) {
        validate(request);

        Note note = find(id);
        note.title = request.title;
        note.content = request.content;
        note.version++;
        note.updatedAt = LocalDateTime.now();
        return note;
    }

    @DeleteMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public synchronized void delete(@PathVariable long id) {
        Note note = find(id);
        note.deleted = true;
        note.version++;
        note.updatedAt = LocalDateTime.now();
    }

    @GetMapping("/notes/{id}")
    public synchronized Note get(@PathVariable long id) {
        return find(id);
    }

    @GetMapping("/notes")
    public synchronized List<Note> list(@RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<Note> result = new ArrayList<Note>();
        for (Note note : notes.values()) {
            if (includeDeleted || !note.deleted) {
                result.add(note);
            }
        }
        result.sort(Comparator.comparingLong(Note::getId));
        return result;
    }

    @GetMapping("/notes/sync")
    public synchronized SyncResult sync(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAfter) {
        List<Note> changed = new ArrayList<Note>();
        for (Note note : notes.values()) {
            if (note.updatedAt.isAfter(updatedAfter)) {
                changed.add(note);
            }
        }
        changed.sort(Comparator.comparing(Note::getUpdatedAt));
        return new SyncResult(updatedAfter, changed.size(), changed);
    }

    private Note find(long id) {
        Note note = notes.get(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found: " + id);
        }
        return note;
    }

    private void validate(NoteRequest request) {
        if (request == null || blank(request.title) || blank(request.content)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title and content are required");
        }
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class NoteRequest {
        public String title;
        public String content;
    }

    public static class Note {
        public long id;
        public String title;
        public String content;
        public boolean deleted;
        public int version;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;

        public Note() {
        }

        public Note(long id,
                    String title,
                    String content,
                    boolean deleted,
                    int version,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.deleted = deleted;
            this.version = version;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getId() {
            return id;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }

    public static class SyncResult {
        public LocalDateTime updatedAfter;
        public int count;
        public List<Note> notes;

        public SyncResult(LocalDateTime updatedAfter, int count, List<Note> notes) {
            this.updatedAfter = updatedAfter;
            this.count = count;
            this.notes = notes;
        }
    }
}
