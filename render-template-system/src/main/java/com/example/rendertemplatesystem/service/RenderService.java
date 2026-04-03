package com.example.rendertemplatesystem.service;

import com.example.rendertemplatesystem.dto.RenderRequest;
import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.exception.InvalidTemplateDataException;
import com.example.rendertemplatesystem.renderer.RendererResolver;
import com.example.rendertemplatesystem.renderer.TemplateRenderer;
import com.example.rendertemplatesystem.template.TemplateDefinition;
import com.example.rendertemplatesystem.template.TemplateProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RenderService {

    private final TemplateProvider templateProvider;
    private final RendererResolver rendererResolver;

    public RenderService(TemplateProvider templateProvider, RendererResolver rendererResolver) {
        this.templateProvider = templateProvider;
        this.rendererResolver = rendererResolver;
    }

    public RenderResponse render(RenderRequest request) {
        TemplateDefinition template = templateProvider.getTemplate(request.getTemplateName());
        validateData(template, request.getData());

        TemplateRenderer renderer = rendererResolver.resolve(request.getFormat());
        return renderer.render(template, request.getData());
    }

    public List<String> getTemplates() {
        return templateProvider.getAvailableTemplates();
    }

    public List<String> getFormats() {
        return Arrays.stream(com.example.rendertemplatesystem.enums.RenderFormat.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private void validateData(TemplateDefinition template, Map<String, Object> data) {
        List<String> missingFields = template.getFieldKeys()
                .stream()
                .filter(field -> isMissing(data.get(field)))
                .collect(Collectors.toList());

        if (!missingFields.isEmpty()) {
            throw new InvalidTemplateDataException("Missing required template data: " + String.join(", ", missingFields));
        }
    }

    private boolean isMissing(Object value) {
        return value == null || String.valueOf(value).trim().isEmpty();
    }
}
