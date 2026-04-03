package com.example.notetakingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SyncResponse {

    private LocalDateTime updatedAfter;
    private Integer count;
    private List<NoteResponse> notes;

    public LocalDateTime getUpdatedAfter() {
        return updatedAfter;
    }

    public void setUpdatedAfter(LocalDateTime updatedAfter) {
        this.updatedAfter = updatedAfter;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<NoteResponse> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteResponse> notes) {
        this.notes = notes;
    }
}

