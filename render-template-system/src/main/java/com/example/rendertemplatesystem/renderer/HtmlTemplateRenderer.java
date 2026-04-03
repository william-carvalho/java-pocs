package com.example.rendertemplatesystem.renderer;

import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.template.TemplateDefinition;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HtmlTemplateRenderer implements TemplateRenderer {

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.HTML;
    }

    @Override
    public RenderResponse render(TemplateDefinition template, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html><head><meta charset=\"UTF-8\"><title>")
                .append(escape(template.getTitle()))
                .append("</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:32px;color:#222;}h1{margin-bottom:24px;}")
                .append("table{border-collapse:collapse;width:100%;max-width:720px;}th,td{border:1px solid #ddd;padding:12px;text-align:left;}")
                .append("th{background:#f5f5f5;width:240px;}")
                .append("</style>")
                .append("</head><body>")
                .append("<h1>").append(escape(template.getTitle())).append("</h1>")
                .append("<table>");

        for (Map.Entry<String, String> entry : template.getFields().entrySet()) {
            html.append("<tr><th>")
                    .append(escape(entry.getValue()))
                    .append("</th><td>")
                    .append(escape(asString(data.get(entry.getKey()))))
                    .append("</td></tr>");
        }

        html.append("</table></body></html>");

        return new RenderResponse(
                template.getName(),
                getFormat(),
                "text/html; charset=UTF-8",
                template.getName() + ".html",
                html.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
