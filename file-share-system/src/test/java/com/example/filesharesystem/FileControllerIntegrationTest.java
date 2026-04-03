package com.example.filesharesystem;

import com.example.filesharesystem.entity.StoredFile;
import com.example.filesharesystem.repository.StoredFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "file.storage.path=target/test-storage",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoredFileRepository storedFileRepository;

    private final Path storagePath = Paths.get("target/test-storage").toAbsolutePath().normalize();

    @BeforeEach
    void setUp() throws IOException {
        storedFileRepository.deleteAll();
        Files.createDirectories(storagePath);
    }

    @Test
    void shouldUploadListSearchRestoreAndDeleteFile() throws Exception {
        byte[] originalContent = "contract-content".getBytes("UTF-8");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contract.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                originalContent
        );

        mockMvc.perform(multipart("/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.originalFileName", is("contract.pdf")))
                .andExpect(jsonPath("$.encrypted", is(true)));

        StoredFile storedFile = storedFileRepository.findAll().get(0);
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(storedFile.getStoragePath()));
        assertFalse(java.util.Arrays.equals(originalContent, encryptedBytes));

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].originalFileName", is("contract.pdf")));

        mockMvc.perform(get("/files/search").param("name", "contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(storedFile.getId().intValue())));

        byte[] restoredContent = mockMvc.perform(get("/files/{id}/restore", storedFile.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"contract.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        assertArrayEquals(originalContent, restoredContent);

        mockMvc.perform(delete("/files/{id}", storedFile.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/files/{id}/restore", storedFile.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectEmptyUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/files").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("file must not be empty")));
    }
}
