package com.example.rendertemplatesystem.template;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TemplateDefinition {

    private final String name;
    private final String title;
    private final LinkedHashMap<String, String> fields;

    public TemplateDefinition(String name, String title, LinkedHashMap<String, String> fields) {
        this.name = name;
        this.title = title;
        this.fields = new LinkedHashMap<String, String>(fields);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public Set<String> getFieldKeys() {
        return Collections.unmodifiableSet(fields.keySet());
    }
}
