package com.example.dontpadclone.dto;

import com.example.dontpadclone.entity.Pad;

import java.time.LocalDateTime;

public class PadResponse {

    private Long id;
    private String slug;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PadResponse from(Pad pad) {
        PadResponse response = new PadResponse();
        response.id = pad.getId();
        response.slug = pad.getSlug();
        response.content = pad.getContent();
        response.createdAt = pad.getCreatedAt();
        response.updatedAt = pad.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

