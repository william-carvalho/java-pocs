package com.example.rendertemplatesystem.renderer;

import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.exception.TemplateRenderException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RendererResolver {

    private final Map<RenderFormat, TemplateRenderer> renderers;

    public RendererResolver(List<TemplateRenderer> availableRenderers) {
        this.renderers = new EnumMap<RenderFormat, TemplateRenderer>(RenderFormat.class);
        for (TemplateRenderer renderer : availableRenderers) {
            this.renderers.put(renderer.getFormat(), renderer);
        }
    }

    public TemplateRenderer resolve(RenderFormat format) {
        TemplateRenderer renderer = renderers.get(format);
        if (renderer == null) {
            throw new TemplateRenderException("Renderer not configured for format: " + format, null);
        }
        return renderer;
    }
}
