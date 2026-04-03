package com.example.rendertemplatesystem.dto;

import com.example.rendertemplatesystem.enums.RenderFormat;

public class RenderResponse {

    private final String templateName;
    private final RenderFormat format;
    private final String contentType;
    private final String fileName;
    private final byte[] content;

    public RenderResponse(String templateName, RenderFormat format, String contentType, String fileName, byte[] content) {
        this.templateName = templateName;
        this.format = format;
        this.contentType = contentType;
        this.fileName = fileName;
        this.content = content;
    }

    public String getTemplateName() {
        return templateName;
    }

    public RenderFormat getFormat() {
        return format;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }
}
