package com.example.rendertemplatesystem.renderer;

import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.template.TemplateDefinition;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CsvTemplateRenderer implements TemplateRenderer {

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.CSV;
    }

    @Override
    public RenderResponse render(TemplateDefinition template, Map<String, Object> data) {
        StringBuilder csv = new StringBuilder();

        Iterator<String> keyIterator = template.getFieldKeys().iterator();
        while (keyIterator.hasNext()) {
            csv.append(keyIterator.next());
            if (keyIterator.hasNext()) {
                csv.append(',');
            }
        }
        csv.append(System.lineSeparator());

        Iterator<String> valueIterator = template.getFieldKeys().iterator();
        while (valueIterator.hasNext()) {
            String key = valueIterator.next();
            csv.append(escapeCsv(asString(data.get(key))));
            if (valueIterator.hasNext()) {
                csv.append(',');
            }
        }

        return new RenderResponse(
                template.getName(),
                getFormat(),
                "text/csv; charset=UTF-8",
                template.getName() + ".csv",
                csv.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
