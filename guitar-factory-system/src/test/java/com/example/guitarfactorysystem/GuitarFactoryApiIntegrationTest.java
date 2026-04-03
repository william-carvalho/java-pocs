package com.example.guitarfactorysystem;

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
class GuitarFactoryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeedModelsAndComponents() throws Exception {
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Strat Style"));

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].name").value("Alder Body"));
    }

    @Test
    void shouldCreateCustomGuitarAndDebitInventory() throws Exception {
        String payload = "{"
                + "\"customerName\":\"William Carvalho\","
                + "\"guitarModelId\":1,"
                + "\"items\":["
                + "{\"componentId\":1,\"quantity\":1},"
                + "{\"componentId\":3,\"quantity\":2},"
                + "{\"componentId\":6,\"quantity\":1}"
                + "]"
                + "}";

        mockMvc.perform(post("/custom-guitars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("William Carvalho"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.basePrice").value(2500))
                .andExpect(jsonPath("$.totalPrice").value(3750))
                .andExpect(jsonPath("$.items.length()").value(3));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantityInStock").value(9))
                .andExpect(jsonPath("$[2].quantityInStock").value(28))
                .andExpect(jsonPath("$[5].quantityInStock").value(19));
    }

    @Test
    void shouldCreateWorkOrderAndRestoreInventoryWhenCancelled() throws Exception {
        Long orderId = createCustomOrder();
        Long workOrderId = createWorkOrder(orderId);

        mockMvc.perform(patch("/work-orders/" + workOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(get("/custom-guitars/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PRODUCTION"));

        mockMvc.perform(patch("/work-orders/" + workOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/custom-guitars/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantityInStock").value(10))
                .andExpect(jsonPath("$[1].quantityInStock").value(10));
    }

    private Long createCustomOrder() throws Exception {
        String payload = "{"
                + "\"customerName\":\"William Carvalho\","
                + "\"guitarModelId\":1,"
                + "\"items\":["
                + "{\"componentId\":1,\"quantity\":1},"
                + "{\"componentId\":2,\"quantity\":1}"
                + "]"
                + "}";

        MvcResult result = mockMvc.perform(post("/custom-guitars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("orderId").asLong();
    }

    private Long createWorkOrder(Long orderId) throws Exception {
        MvcResult result = mockMvc.perform(post("/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customGuitarOrderId\":" + orderId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }
}
