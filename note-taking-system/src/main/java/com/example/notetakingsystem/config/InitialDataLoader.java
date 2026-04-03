package com.example.notetakingsystem.config;

import com.example.notetakingsystem.entity.Note;
import com.example.notetakingsystem.entity.NoteStatus;
import com.example.notetakingsystem.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadInitialNotes(NoteRepository noteRepository) {
        return args -> {
            if (noteRepository.count() > 0) {
                return;
            }

            noteRepository.save(createNote("Welcome", "This is your first note"));
            noteRepository.save(createNote("Tasks", "Finish POC"));
            noteRepository.save(createNote("Ideas", "Build sync endpoint"));
        };
    }

    private Note createNote(String title, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setStatus(NoteStatus.ACTIVE);
        note.setDeleted(Boolean.FALSE);
        note.setVersion(1L);
        return note;
    }
}
