package com.example.loggerbuilderroutersystem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.logging.fs.path=target/test-logs/app.log")
public class LoggerBuilderRouterSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListDestinations() throws Exception {
        mockMvc.perform(get("/logs/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("FS"));
    }

    @Test
    void shouldPublishSyncLogToFileSystem() throws Exception {
        String payload = "{"
                + "\"message\":\"User created successfully\","
                + "\"level\":\"INFO\","
                + "\"destination\":\"FS\","
                + "\"mode\":\"SYNC\","
                + "\"metadata\":{\"userId\":\"123\"}"
                + "}";

        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.destination").value("FS"))
                .andExpect(jsonPath("$.mode").value("SYNC"));
    }
}
