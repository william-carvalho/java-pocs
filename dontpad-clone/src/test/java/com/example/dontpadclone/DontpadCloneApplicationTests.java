package com.example.dontpadclone;

import com.example.dontpadclone.dto.CreatePadRequest;
import com.example.dontpadclone.dto.PadResponse;
import com.example.dontpadclone.dto.UpdatePadRequest;
import com.example.dontpadclone.exception.BusinessException;
import com.example.dontpadclone.exception.NotFoundException;
import com.example.dontpadclone.service.PadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DontpadCloneApplicationTests {

    @Autowired
    private PadService padService;

    @Test
    void shouldCreatePadExplicitly() {
        CreatePadRequest request = new CreatePadRequest();
        request.setSlug("my-note");
        request.setContent("Hello world");

        PadResponse response = padService.create(request);

        assertEquals("my-note", response.getSlug());
        assertEquals("Hello world", response.getContent());
        assertTrue(response.getCreatedAt() != null);
        assertTrue(response.getUpdatedAt() != null);
    }

    @Test
    void shouldCreatePadAutomaticallyWhenGettingBySlug() {
        PadResponse response = padService.getOrCreateBySlug("auto-created");

        assertEquals("auto-created", response.getSlug());
        assertEquals("", response.getContent());
    }

    @Test
    void shouldUpdatePadBySlug() {
        UpdatePadRequest request = new UpdatePadRequest();
        request.setContent("Updated content");

        PadResponse response = padService.upsertBySlug("editable-pad", request);

        assertEquals("editable-pad", response.getSlug());
        assertEquals("Updated content", response.getContent());
    }

    @Test
    void shouldDeletePadBySlug() {
        CreatePadRequest request = new CreatePadRequest();
        request.setSlug("to-delete");
        request.setContent("Delete me");
        padService.create(request);

        padService.deleteBySlug("to-delete");

        assertThrows(NotFoundException.class, () -> padService.deleteBySlug("to-delete"));
    }

    @Test
    void shouldRejectInvalidSlug() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> padService.getOrCreateBySlug("invalid slug"));

        assertEquals("Slug must match [a-zA-Z0-9_-]+", exception.getMessage());
    }
}

