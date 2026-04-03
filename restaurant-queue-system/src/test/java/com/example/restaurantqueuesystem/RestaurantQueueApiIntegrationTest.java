package com.example.restaurantqueuesystem;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantQueueApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeedDishes() throws Exception {
        mockMvc.perform(get("/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Burger"));
    }

    @Test
    void shouldCreateOrderWithCalculatedEstimate() throws Exception {
        String payload = "{"
                + "\"customerName\":\"William\","
                + "\"items\":["
                + "{\"dishId\":1,\"quantity\":1},"
                + "{\"dishId\":3,\"quantity\":1}"
                + "]"
                + "}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.totalPreparationTime").value(23))
                .andExpect(jsonPath("$.estimatedStartInMinutes").value(0))
                .andExpect(jsonPath("$.estimatedCompletionInMinutes").value(23))
                .andExpect(jsonPath("$.remainingTimeInMinutes").value(23))
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void shouldRecalculateQueueWhenStatusChanges() throws Exception {
        Long firstOrderId = createOrder("{\"customerName\":\"Ana\",\"items\":[{\"dishId\":1,\"quantity\":1},{\"dishId\":3,\"quantity\":1}]}");
        Long secondOrderId = createOrder("{\"customerName\":\"Bob\",\"items\":[{\"dishId\":2,\"quantity\":2}]}");

        mockMvc.perform(get("/orders/" + secondOrderId + "/estimate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(2))
                .andExpect(jsonPath("$.totalPreparationTime").value(40))
                .andExpect(jsonPath("$.estimatedStartInMinutes").value(23))
                .andExpect(jsonPath("$.estimatedCompletionInMinutes").value(63))
                .andExpect(jsonPath("$.remainingTimeInMinutes").value(63));

        mockMvc.perform(patch("/orders/" + firstOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.queuePosition").doesNotExist());

        mockMvc.perform(get("/orders/" + secondOrderId + "/estimate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.estimatedStartInMinutes").value(0))
                .andExpect(jsonPath("$.estimatedCompletionInMinutes").value(40))
                .andExpect(jsonPath("$.remainingTimeInMinutes").value(40));

        mockMvc.perform(get("/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(secondOrderId))
                .andExpect(jsonPath("$[0].queuePosition").value(1));
    }

    private Long createOrder(String payload) throws Exception {
        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("orderId").asLong();
    }
}
