package com.example.calendarsystem.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SuggestMeetingRequest {

    @NotNull(message = "personOneId is required")
    private Long personOneId;

    @NotNull(message = "personTwoId is required")
    private Long personTwoId;

    @NotNull(message = "searchStart is required")
    private LocalDateTime searchStart;

    @NotNull(message = "searchEnd is required")
    private LocalDateTime searchEnd;

    @NotNull(message = "durationMinutes is required")
    @Min(value = 1, message = "durationMinutes must be greater than zero")
    private Integer durationMinutes;

    public Long getPersonOneId() {
        return personOneId;
    }

    public void setPersonOneId(Long personOneId) {
        this.personOneId = personOneId;
    }

    public Long getPersonTwoId() {
        return personTwoId;
    }

    public void setPersonTwoId(Long personTwoId) {
        this.personTwoId = personTwoId;
    }

    public LocalDateTime getSearchStart() {
        return searchStart;
    }

    public void setSearchStart(LocalDateTime searchStart) {
        this.searchStart = searchStart;
    }

    public LocalDateTime getSearchEnd() {
        return searchEnd;
    }

    public void setSearchEnd(LocalDateTime searchEnd) {
        this.searchEnd = searchEnd;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}

