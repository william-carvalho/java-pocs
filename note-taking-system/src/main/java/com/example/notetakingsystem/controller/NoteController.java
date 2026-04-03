package com.example.notetakingsystem.controller;

import com.example.notetakingsystem.dto.CreateNoteRequest;
import com.example.notetakingsystem.dto.NoteResponse;
import com.example.notetakingsystem.dto.SyncResponse;
import com.example.notetakingsystem.dto.UpdateNoteRequest;
import com.example.notetakingsystem.service.NoteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notes")
@Validated
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody CreateNoteRequest request) {
        return ResponseEntity.ok(noteService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> list(@RequestParam(value = "includeDeleted", defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(includeDeleted ? noteService.listAll() : noteService.listActive());
    }

    @GetMapping("/all")
    public ResponseEntity<List<NoteResponse>> listAll() {
        return ResponseEntity.ok(noteService.listAll());
    }

    @GetMapping("/sync")
    public ResponseEntity<SyncResponse> sync(
            @RequestParam("updatedAfter")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime updatedAfter) {
        return ResponseEntity.ok(noteService.sync(updatedAfter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateNoteRequest request) {
        return ResponseEntity.ok(noteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

