package com.example.taxsystem.grocery.tests;

import com.example.taxsystem.grocery.api.CreateGroceryItemRequest;
import com.example.taxsystem.grocery.api.GroceryController;
import com.example.taxsystem.grocery.api.GroceryItemResponse;
import com.example.taxsystem.grocery.application.GroceryService;
import com.example.taxsystem.grocery.domain.GroceryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroceryController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroceryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroceryService groceryService;

    @Test
    void shouldCreateItem() throws Exception {
        GroceryItemResponse response = new GroceryItemResponse();
        response.setId(10L);
        response.setName("bread");
        response.setStatus(GroceryStatus.PENDING);

        when(groceryService.create(any(CreateGroceryItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/grocery/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"bread\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("bread"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldListItemsByStatus() throws Exception {
        GroceryItemResponse response = new GroceryItemResponse();
        response.setId(11L);
        response.setName("milk");
        response.setStatus(GroceryStatus.DONE);

        when(groceryService.list(null, GroceryStatus.DONE)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/grocery/items").param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].status").value("DONE"));
    }
}
