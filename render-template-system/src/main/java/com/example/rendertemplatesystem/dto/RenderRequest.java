package com.example.rendertemplatesystem.dto;

import com.example.rendertemplatesystem.enums.RenderFormat;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RenderRequest {

    @NotBlank(message = "templateName is required")
    private String templateName;

    @NotNull(message = "format is required")
    private RenderFormat format;

    @NotNull(message = "data is required")
    private Map<String, Object> data;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public RenderFormat getFormat() {
        return format;
    }

    public void setFormat(RenderFormat format) {
        this.format = format;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
