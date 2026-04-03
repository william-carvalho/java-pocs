package com.example.logisticfreightsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FreightApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListTransportTypes() throws Exception {
        mockMvc.perform(get("/freight/transport-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("BOAT"))
                .andExpect(jsonPath("$[1]").value("TRUCK"))
                .andExpect(jsonPath("$[2]").value("RAIL"));
    }

    @Test
    void shouldCalculateFreightUsingSeedRule() throws Exception {
        String payload = "{"
                + "\"transportType\":\"TRUCK\","
                + "\"width\":2.0,"
                + "\"height\":1.5,"
                + "\"length\":3.0,"
                + "\"weight\":100.0,"
                + "\"referenceDate\":\"2026-04-01\""
                + "}";

        mockMvc.perform(post("/freight/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transportType").value("TRUCK"))
                .andExpect(jsonPath("$.volume").value(9))
                .andExpect(jsonPath("$.finalPrice").value(150.18));
    }
}
