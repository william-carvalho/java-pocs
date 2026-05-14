package com.example.rendertemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RenderTemplateTest {

    @Test
    void rendersSameTemplateAsHtml() {
        RenderTemplate renderer = renderer();

        RenderTemplate.RenderResult result = renderer.render("invoice", data(), RenderTemplate.Format.HTML);

        assertEquals(RenderTemplate.Format.HTML, result.format());
        assertEquals("text/html; charset=UTF-8", result.contentType());
        assertTrue(result.asText().contains("Invoice INV-1 for Ana &amp; Co total 99.90"));
    }

    @Test
    void rendersSameTemplateAsCsv() {
        RenderTemplate renderer = renderer();

        RenderTemplate.RenderResult result = renderer.render("invoice", data(), RenderTemplate.Format.CSV);

        assertEquals(RenderTemplate.Format.CSV, result.format());
        assertEquals("text/csv; charset=UTF-8", result.contentType());
        assertTrue(result.asText().contains("\"template\",\"rendered\",\"number\",\"customer\",\"total\""));
        assertTrue(result.asText().contains("\"invoice\",\"Invoice INV-1 for Ana & Co total 99.90\""));
    }

    @Test
    void rendersSameTemplateAsPdfBytes() {
        RenderTemplate renderer = renderer();

        RenderTemplate.RenderResult result = renderer.render("invoice", data(), RenderTemplate.Format.PDF);
        String pdf = new String(result.asBytes(), StandardCharsets.UTF_8);

        assertEquals(RenderTemplate.Format.PDF, result.format());
        assertEquals("application/pdf", result.contentType());
        assertTrue(pdf.startsWith("%PDF-1.4"));
        assertTrue(pdf.contains("Invoice INV-1 for Ana & Co total 99.90"));
        assertTrue(pdf.endsWith("%%EOF"));
    }

    @Test
    void rejectsMissingTemplateValue() {
        RenderTemplate renderer = renderer();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("number", "INV-1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                renderer.render("invoice", data, RenderTemplate.Format.HTML));

        assertEquals("Missing template value: customer", exception.getMessage());
    }

    private static RenderTemplate renderer() {
        return RenderTemplate.builder()
                .template("invoice", "Invoice {{number}} for {{customer}} total {{total}}")
                .build();
    }

    private static Map<String, Object> data() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("number", "INV-1");
        data.put("customer", "Ana & Co");
        data.put("total", "99.90");
        return data;
    }
}
