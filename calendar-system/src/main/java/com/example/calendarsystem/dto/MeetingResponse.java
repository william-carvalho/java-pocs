package com.example.calendarsystem.dto;

import com.example.calendarsystem.entity.MeetingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class MeetingResponse {

    private Long id;
    private String title;
    private String description;
    private PersonResponse organizer;
    private List<PersonResponse> participants;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private MeetingStatus status;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PersonResponse getOrganizer() {
        return organizer;
    }

    public void setOrganizer(PersonResponse organizer) {
        this.organizer = organizer;
    }

    public List<PersonResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PersonResponse> participants) {
        this.participants = participants;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

