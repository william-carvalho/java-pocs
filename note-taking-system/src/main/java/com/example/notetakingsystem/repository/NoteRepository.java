package com.example.notetakingsystem.repository;

import com.example.notetakingsystem.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByDeletedFalseOrderByUpdatedAtDesc();

    List<Note> findAllByOrderByUpdatedAtDesc();

    List<Note> findByUpdatedAtGreaterThanEqualOrderByUpdatedAtAsc(LocalDateTime updatedAfter);
}

