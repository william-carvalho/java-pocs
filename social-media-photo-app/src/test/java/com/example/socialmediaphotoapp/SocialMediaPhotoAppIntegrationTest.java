package com.example.socialmediaphotoapp;

import com.example.socialmediaphotoapp.repository.CommentRepository;
import com.example.socialmediaphotoapp.repository.PhotoPostRepository;
import com.example.socialmediaphotoapp.repository.TagRepository;
import com.example.socialmediaphotoapp.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "photo.storage.path=target/test-photo-storage",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SocialMediaPhotoAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PhotoPostRepository photoPostRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws IOException {
        commentRepository.deleteAll();
        photoPostRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        Files.createDirectories(Paths.get("target/test-photo-storage"));
    }

    @Test
    void shouldCreateUserPublishPhotoTagCommentTimelineAndDelete() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"William Carvalho\",\"username\":\"william\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("william")));

        Long userId = userRepository.findAll().get(0).getId();
        byte[] imageBytes = "fake-jpeg-content".getBytes("UTF-8");
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

        mockMvc.perform(multipart("/photos")
                        .file(file)
                        .param("userId", userId.toString())
                        .param("caption", "Minha primeira foto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caption", is("Minha primeira foto")))
                .andExpect(jsonPath("$.commentsCount", is(0)));

        Long photoId = photoPostRepository.findAll().get(0).getId();
        Path storedPath = Paths.get(photoPostRepository.findAll().get(0).getStoragePath());
        assertTrue(Files.exists(storedPath));

        mockMvc.perform(post("/photos/{id}/tags", photoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tags\":[\"travel\",\"beach\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", hasSize(2)));

        mockMvc.perform(post("/photos/{id}/comments", photoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"text\":\"Muito boa!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Muito boa!")))
                .andExpect(jsonPath("$.user.username", is("william")));

        mockMvc.perform(get("/photos/{id}", photoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentsCount", is(1)))
                .andExpect(jsonPath("$.tags", hasSize(2)));

        mockMvc.perform(get("/photos/{id}/content", photoId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"foto.jpg\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));

        mockMvc.perform(get("/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.items[0].photoId", is(photoId.intValue())))
                .andExpect(jsonPath("$.items[0].comments", hasSize(1)))
                .andExpect(jsonPath("$.items[0].tags", hasSize(2)));

        mockMvc.perform(get("/timeline").param("tag", "travel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)));

        mockMvc.perform(delete("/photos/{id}", photoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        mockMvc.perform(get("/photos/{id}", photoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectEmptyOrNonImageUpload() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"William Carvalho\",\"username\":\"william\"}"))
                .andExpect(status().isOk());

        Long userId = userRepository.findAll().get(0).getId();

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        mockMvc.perform(multipart("/photos")
                        .file(emptyFile)
                        .param("userId", userId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("file must not be empty")));

        MockMultipartFile textFile = new MockMultipartFile("file", "note.txt", MediaType.TEXT_PLAIN_VALUE, "abc".getBytes("UTF-8"));
        mockMvc.perform(multipart("/photos")
                        .file(textFile)
                        .param("userId", userId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("file contentType must be an image")));
    }
}

