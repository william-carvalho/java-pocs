package com.example.rendertemplatesystem.template;

import com.example.rendertemplatesystem.exception.TemplateNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TemplateProvider {

    private final Map<String, TemplateDefinition> templates;

    public TemplateProvider() {
        this.templates = new LinkedHashMap<String, TemplateDefinition>();
        registerInvoice();
    }

    public TemplateDefinition getTemplate(String templateName) {
        TemplateDefinition template = templates.get(normalize(templateName));
        if (template == null) {
            throw new TemplateNotFoundException("Template not found: " + templateName);
        }
        return template;
    }

    public List<String> getAvailableTemplates() {
        return Collections.unmodifiableList(new ArrayList<String>(templates.keySet()));
    }

    private void registerInvoice() {
        LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
        fields.put("customerName", "Customer Name");
        fields.put("documentNumber", "Document Number");
        fields.put("amount", "Amount");
        fields.put("dueDate", "Due Date");

        TemplateDefinition invoice = new TemplateDefinition("invoice", "Invoice Report", fields);
        templates.put(invoice.getName(), invoice);
    }

    private String normalize(String templateName) {
        return templateName == null ? null : templateName.trim().toLowerCase(Locale.ROOT);
    }
}
