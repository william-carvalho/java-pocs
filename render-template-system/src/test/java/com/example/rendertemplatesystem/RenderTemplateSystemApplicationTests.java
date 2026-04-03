package com.example.rendertemplatesystem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RenderTemplateSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListTemplates() throws Exception {
        mockMvc.perform(get("/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("invoice"));
    }

    @Test
    void shouldRenderHtml() throws Exception {
        mockMvc.perform(post("/render")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload("HTML")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Render-Format", "HTML"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("William Carvalho")));
    }

    @Test
    void shouldRenderPdf() throws Exception {
        mockMvc.perform(post("/render")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload("PDF")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Render-Format", "PDF"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void shouldRejectUnknownTemplate() throws Exception {
        String payload = "{"
                + "\"templateName\":\"unknown\","
                + "\"format\":\"HTML\","
                + "\"data\":{"
                + "\"customerName\":\"William Carvalho\","
                + "\"documentNumber\":\"INV-2026-001\","
                + "\"amount\":\"1500.00\","
                + "\"dueDate\":\"2026-04-10\""
                + "}"
                + "}";

        mockMvc.perform(post("/render")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Template not found: unknown"));
    }

    private String validPayload(String format) {
        return "{"
                + "\"templateName\":\"invoice\","
                + "\"format\":\"" + format + "\","
                + "\"data\":{"
                + "\"customerName\":\"William Carvalho\","
                + "\"documentNumber\":\"INV-2026-001\","
                + "\"amount\":\"1500.00\","
                + "\"dueDate\":\"2026-04-10\""
                + "}"
                + "}";
    }
}
