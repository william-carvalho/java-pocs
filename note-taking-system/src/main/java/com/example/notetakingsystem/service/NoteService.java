package com.example.notetakingsystem.service;

import com.example.notetakingsystem.dto.CreateNoteRequest;
import com.example.notetakingsystem.dto.NoteResponse;
import com.example.notetakingsystem.dto.SyncResponse;
import com.example.notetakingsystem.dto.UpdateNoteRequest;
import com.example.notetakingsystem.entity.Note;
import com.example.notetakingsystem.entity.NoteStatus;
import com.example.notetakingsystem.exception.ResourceNotFoundException;
import com.example.notetakingsystem.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public NoteResponse create(CreateNoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        note.setStatus(NoteStatus.ACTIVE);
        note.setDeleted(Boolean.FALSE);
        note.setVersion(1L);

        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> listActive() {
        return noteRepository.findByDeletedFalseOrderByUpdatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> listAll() {
        return noteRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteResponse findById(Long id) {
        return toResponse(findExisting(id));
    }

    @Transactional
    public NoteResponse update(Long id, UpdateNoteRequest request) {
        Note note = findExisting(id);
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        touch(note);
        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long id) {
        Note note = findExisting(id);
        if (!Boolean.TRUE.equals(note.getDeleted())) {
            note.setDeleted(Boolean.TRUE);
            touch(note);
            noteRepository.save(note);
        }
    }

    @Transactional(readOnly = true)
    public SyncResponse sync(LocalDateTime updatedAfter) {
        if (updatedAfter == null) {
            throw new IllegalArgumentException("updatedAfter is required");
        }

        List<NoteResponse> notes = noteRepository.findByUpdatedAtGreaterThanEqualOrderByUpdatedAtAsc(updatedAfter)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        SyncResponse response = new SyncResponse();
        response.setUpdatedAfter(updatedAfter);
        response.setCount(notes.size());
        response.setNotes(notes);
        return response;
    }

    private Note findExisting(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));
    }

    private void touch(Note note) {
        note.setUpdatedAt(LocalDateTime.now());
        note.setVersion(note.getVersion() + 1);
    }

    private NoteResponse toResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setStatus(note.getStatus());
        response.setDeleted(note.getDeleted());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        response.setVersion(note.getVersion());
        return response;
    }
}

