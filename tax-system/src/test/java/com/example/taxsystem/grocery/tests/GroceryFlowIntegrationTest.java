package com.example.taxsystem.grocery.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GroceryFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateDoRedoAndListItem() throws Exception {
        mockMvc.perform(post("/api/grocery/items")
                        .with(httpBasic("writer", "writer-secret"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"apples\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("apples"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(put("/api/grocery/items/1/do")
                        .with(httpBasic("writer", "writer-secret")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(put("/api/grocery/items/1/redo")
                        .with(httpBasic("writer", "writer-secret")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/grocery/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("apples"));
    }
}
