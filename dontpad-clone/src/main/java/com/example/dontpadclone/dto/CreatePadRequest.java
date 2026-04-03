package com.example.dontpadclone.dto;

import javax.validation.constraints.NotBlank;

public class CreatePadRequest {

    @NotBlank
    private String slug;

    private String content;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

