package com.example.rendertemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RenderTemplate {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([A-Za-z0-9_.-]+)\\s*}}");

    private final Map<String, String> templates;

    private RenderTemplate(Map<String, String> templates) {
        this.templates = Collections.unmodifiableMap(new LinkedHashMap<String, String>(templates));
    }

    public static Builder builder() {
        return new Builder();
    }

    public RenderResult render(String templateName, Map<String, ?> data, Format format) {
        String template = templates.get(requireText(templateName, "templateName"));
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }

        Map<String, ?> safeData = data == null ? Collections.<String, Object>emptyMap() : data;
        Format safeFormat = Objects.requireNonNull(format, "format");
        String rendered = replacePlaceholders(template, safeData);

        if (safeFormat == Format.HTML) {
            String html = "<!doctype html><html><body><pre>"
                    + escapeHtml(rendered)
                    + "</pre></body></html>";
            return RenderResult.text(Format.HTML, "text/html; charset=UTF-8", html);
        }

        if (safeFormat == Format.CSV) {
            String csv = toCsv(templateName, safeData, rendered);
            return RenderResult.text(Format.CSV, "text/csv; charset=UTF-8", csv);
        }

        return RenderResult.bytes(Format.PDF, "application/pdf", toPdf(rendered));
    }

    public Map<String, String> templates() {
        return templates;
    }

    private String replacePlaceholders(String template, Map<String, ?> data) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.get(key);
            if (value == null) {
                throw new IllegalArgumentException("Missing template value: " + key);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(String.valueOf(value)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String toCsv(String templateName, Map<String, ?> data, String rendered) {
        StringBuilder header = new StringBuilder(csvValue("template")).append(",").append(csvValue("rendered"));
        StringBuilder row = new StringBuilder(csvValue(templateName)).append(",").append(csvValue(rendered));

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            header.append(",").append(csvValue(entry.getKey()));
            row.append(",").append(csvValue(String.valueOf(entry.getValue())));
        }

        return header.append(System.lineSeparator()).append(row).append(System.lineSeparator()).toString();
    }

    private static byte[] toPdf(String text) {
        String escaped = escapePdfText(text);
        String stream = "BT /F1 12 Tf 72 720 Td (" + escaped + ") Tj ET";
        byte[] streamBytes = stream.getBytes(StandardCharsets.UTF_8);

        String obj1 = "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n";
        String obj2 = "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n";
        String obj3 = "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                + "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj\n";
        String obj4 = "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n";
        String obj5 = "5 0 obj << /Length " + streamBytes.length + " >> stream\n"
                + stream + "\nendstream endobj\n";

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        int xref1 = pdf.length();
        pdf.append(obj1);
        int xref2 = pdf.length();
        pdf.append(obj2);
        int xref3 = pdf.length();
        pdf.append(obj3);
        int xref4 = pdf.length();
        pdf.append(obj4);
        int xref5 = pdf.length();
        pdf.append(obj5);
        int xrefStart = pdf.length();

        pdf.append("xref\n0 6\n");
        pdf.append("0000000000 65535 f \n");
        pdf.append(formatXref(xref1)).append(" 00000 n \n");
        pdf.append(formatXref(xref2)).append(" 00000 n \n");
        pdf.append(formatXref(xref3)).append(" 00000 n \n");
        pdf.append(formatXref(xref4)).append(" 00000 n \n");
        pdf.append(formatXref(xref5)).append(" 00000 n \n");
        pdf.append("trailer << /Size 6 /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefStart).append("\n%%EOF");

        return pdf.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String formatXref(int value) {
        String text = String.valueOf(value);
        StringBuilder padded = new StringBuilder();
        for (int i = text.length(); i < 10; i++) {
            padded.append('0');
        }
        return padded.append(text).toString();
    }

    private static String csvValue(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String escapePdfText(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    public static final class Builder {
        private final Map<String, String> templates = new LinkedHashMap<String, String>();

        public Builder template(String name, String template) {
            templates.put(requireText(name, "name"), requireText(template, "template"));
            return this;
        }

        public RenderTemplate build() {
            if (templates.isEmpty()) {
                throw new IllegalStateException("At least one template is required");
            }
            return new RenderTemplate(templates);
        }
    }

    public static final class RenderResult {
        private final Format format;
        private final String contentType;
        private final byte[] body;

        private RenderResult(Format format, String contentType, byte[] body) {
            this.format = format;
            this.contentType = contentType;
            this.body = body.clone();
        }

        private static RenderResult text(Format format, String contentType, String body) {
            return new RenderResult(format, contentType, body.getBytes(StandardCharsets.UTF_8));
        }

        private static RenderResult bytes(Format format, String contentType, byte[] body) {
            return new RenderResult(format, contentType, body);
        }

        public Format format() {
            return format;
        }

        public String contentType() {
            return contentType;
        }

        public String asText() {
            return new String(body, StandardCharsets.UTF_8);
        }

        public byte[] asBytes() {
            return body.clone();
        }
    }

    public enum Format {
        HTML, PDF, CSV
    }
}
