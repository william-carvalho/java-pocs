package com.example.notetakingsingleclass;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NoteTakingSingleClassApplicationTest {

    @Test
    void addsAndListsNotes() {
        NoteTakingSingleClassApplication app = new NoteTakingSingleClassApplication();

        NoteTakingSingleClassApplication.Note note = app.add(request("Shopping", "Milk"));

        assertThat(note.id).isEqualTo(1);
        assertThat(note.deleted).isFalse();
        assertThat(note.version).isEqualTo(1);
        assertThat(app.list(false)).hasSize(1);
    }

    @Test
    void editsAndSavesNotes() throws Exception {
        NoteTakingSingleClassApplication app = new NoteTakingSingleClassApplication();
        NoteTakingSingleClassApplication.Note note = app.add(request("Old", "Old content"));
        LocalDateTime firstUpdatedAt = note.updatedAt;

        Thread.sleep(5);
        NoteTakingSingleClassApplication.Note edited = app.edit(note.id, request("New", "New content"));

        assertThat(edited.title).isEqualTo("New");
        assertThat(edited.content).isEqualTo("New content");
        assertThat(edited.version).isEqualTo(2);
        assertThat(edited.updatedAt).isAfter(firstUpdatedAt);

        LocalDateTime editedAt = edited.updatedAt;
        Thread.sleep(5);
        NoteTakingSingleClassApplication.Note saved = app.save(note.id);

        assertThat(saved.version).isEqualTo(2);
        assertThat(saved.updatedAt).isAfter(editedAt);
    }

    @Test
    void deletesNotesLogically() {
        NoteTakingSingleClassApplication app = new NoteTakingSingleClassApplication();
        NoteTakingSingleClassApplication.Note note = app.add(request("Remove", "Delete me"));

        app.delete(note.id);

        assertThat(app.list(false)).isEmpty();
        assertThat(app.list(true)).hasSize(1);
        assertThat(app.get(note.id).deleted).isTrue();
        assertThat(app.get(note.id).version).isEqualTo(2);
    }

    @Test
    void syncReturnsChangedNotesIncludingDeletedOnes() throws Exception {
        NoteTakingSingleClassApplication app = new NoteTakingSingleClassApplication();
        NoteTakingSingleClassApplication.Note oldNote = app.add(request("Old", "Not changed later"));
        LocalDateTime checkpoint = oldNote.updatedAt;

        Thread.sleep(5);
        NoteTakingSingleClassApplication.Note changed = app.add(request("Changed", "Created after checkpoint"));
        NoteTakingSingleClassApplication.Note deleted = app.add(request("Deleted", "Will be deleted"));
        app.delete(deleted.id);

        NoteTakingSingleClassApplication.SyncResult result = app.sync(checkpoint);

        assertThat(result.updatedAfter).isEqualTo(checkpoint);
        assertThat(result.count).isEqualTo(2);
        assertThat(result.notes).extracting("id").containsExactly(changed.id, deleted.id);
        assertThat(result.notes.get(1).deleted).isTrue();
    }

    @Test
    void validatesRequiredFields() {
        NoteTakingSingleClassApplication app = new NoteTakingSingleClassApplication();

        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                app.add(request("", "Content"));
            }
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("title and content are required");
    }

    private NoteTakingSingleClassApplication.NoteRequest request(String title, String content) {
        NoteTakingSingleClassApplication.NoteRequest request = new NoteTakingSingleClassApplication.NoteRequest();
        request.title = title;
        request.content = content;
        return request;
    }
}
