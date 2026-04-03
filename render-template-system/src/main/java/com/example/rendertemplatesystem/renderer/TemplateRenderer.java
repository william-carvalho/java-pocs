package com.example.rendertemplatesystem.renderer;

import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.template.TemplateDefinition;
import java.util.Map;

public interface TemplateRenderer {

    RenderFormat getFormat();

    RenderResponse render(TemplateDefinition template, Map<String, Object> data);
}
