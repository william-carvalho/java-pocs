package com.example.grocerytodolistsystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroceryTodoListApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeedItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Milk"));
    }

    @Test
    void shouldCreateAndUpdateItemStatus() throws Exception {
        Long itemId = createItem("{\"name\":\"Cheese\",\"quantity\":\"500g\"}");

        mockMvc.perform(patch("/items/" + itemId + "/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(patch("/items/" + itemId + "/redo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(patch("/items/" + itemId + "/do"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cheese"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        Long itemId = createItem("{\"name\":\"Coffee\",\"quantity\":\"1 bag\"}");

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/items/" + itemId))
                .andExpect(status().isNotFound());
    }

    private Long createItem(String payload) throws Exception {
        MvcResult result = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }
}
