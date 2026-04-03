package com.example.dontpadclone.dto;

import com.example.dontpadclone.entity.Pad;

import java.time.LocalDateTime;

public class PadSummaryResponse {

    private Long id;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PadSummaryResponse from(Pad pad) {
        PadSummaryResponse response = new PadSummaryResponse();
        response.id = pad.getId();
        response.slug = pad.getSlug();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

