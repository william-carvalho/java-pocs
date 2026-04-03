package com.example.notetakingsystem;

import com.example.notetakingsystem.entity.Note;
import com.example.notetakingsystem.entity.NoteStatus;
import com.example.notetakingsystem.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
    void shouldCreateUpdateDeleteAndSyncNote() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Shopping List\",\"content\":\"Milk, Bread, Eggs\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title", is("Shopping List")))
                .andExpect(jsonPath("$.version", is(1)));

        Long noteId = noteRepository.findAll().get(0).getId();

        mockMvc.perform(put("/notes/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Shopping List Updated\",\"content\":\"Milk, Bread, Eggs, Coffee\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Shopping List Updated")))
                .andExpect(jsonPath("$.version", is(2)));

        mockMvc.perform(delete("/notes/{id}", noteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/notes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].deleted", is(true)))
                .andExpect(jsonPath("$[0].version", is(3)));

        mockMvc.perform(get("/notes/sync")
                        .param("updatedAfter", "2000-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.notes[0].id", is(noteId.intValue())))
                .andExpect(jsonPath("$.notes[0].deleted", is(true)));
    }

    @Test
    void shouldListOnlyActiveNotesByDefaultAndIncludeDeletedWhenRequested() throws Exception {
        noteRepository.save(createNote("Active", "Visible note", false, 1L));
        noteRepository.save(createNote("Deleted", "Hidden note", true, 2L));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Active")));

        mockMvc.perform(get("/notes").param("includeDeleted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldValidateRequiredFields() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private Note createNote(String title, String content, boolean deleted, long version) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setStatus(NoteStatus.ACTIVE);
        note.setDeleted(deleted);
        note.setVersion(version);
        note.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        note.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
        return note;
    }
}

