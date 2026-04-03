package com.example.ticketsystem;

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
class TicketSystemApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeedData() throws Exception {
        mockMvc.perform(get("/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Rock Night"));

        mockMvc.perform(get("/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Arena Floripa"));

        mockMvc.perform(get("/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldSellTicketsAndUpdateAvailability() throws Exception {
        String payload = "{"
                + "\"sessionId\":1,"
                + "\"customerName\":\"William Carvalho\","
                + "\"items\":["
                + "{\"zoneId\":1,\"seatNumber\":\"A10\"},"
                + "{\"zoneId\":1,\"seatNumber\":\"A11\"}"
                + "]"
                + "}";

        mockMvc.perform(post("/tickets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value(1))
                .andExpect(jsonPath("$.totalTickets").value(2))
                .andExpect(jsonPath("$.tickets[0].seatNumber").value("A10"))
                .andExpect(jsonPath("$.tickets[1].seatNumber").value("A11"));

        mockMvc.perform(get("/sessions/1/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(1))
                .andExpect(jsonPath("$.zones[0].zoneName").value("VIP"))
                .andExpect(jsonPath("$.zones[0].soldCount").value(2))
                .andExpect(jsonPath("$.zones[0].availableCount").value(48))
                .andExpect(jsonPath("$.zones[0].occupiedSeats[0]").value("A10"))
                .andExpect(jsonPath("$.zones[0].occupiedSeats[1]").value("A11"));
    }

    @Test
    void shouldCancelTicketAndFreeSeat() throws Exception {
        Long ticketId = sellSingleTicket();

        mockMvc.perform(patch("/tickets/" + ticketId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/sessions/1/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zones[0].soldCount").value(0))
                .andExpect(jsonPath("$.zones[0].availableCount").value(50))
                .andExpect(jsonPath("$.zones[0].occupiedSeats.length()").value(0));

        mockMvc.perform(post("/tickets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"customerName\":\"Ana\",\"items\":[{\"zoneId\":1,\"seatNumber\":\"A10\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tickets[0].seatNumber").value("A10"));
    }

    private Long sellSingleTicket() throws Exception {
        MvcResult result = mockMvc.perform(post("/tickets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"customerName\":\"William Carvalho\",\"items\":[{\"zoneId\":1,\"seatNumber\":\"A10\"}]}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("tickets").get(0).get("ticketId").asLong();
    }
}
