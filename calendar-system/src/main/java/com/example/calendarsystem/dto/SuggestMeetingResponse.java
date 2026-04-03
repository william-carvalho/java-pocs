package com.example.calendarsystem.dto;

import java.time.LocalDateTime;

public class SuggestMeetingResponse {

    private Long personOneId;
    private Long personTwoId;
    private LocalDateTime suggestedStart;
    private LocalDateTime suggestedEnd;
    private Integer durationMinutes;
    private String message;

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

    public LocalDateTime getSuggestedStart() {
        return suggestedStart;
    }

    public void setSuggestedStart(LocalDateTime suggestedStart) {
        this.suggestedStart = suggestedStart;
    }

    public LocalDateTime getSuggestedEnd() {
        return suggestedEnd;
    }

    public void setSuggestedEnd(LocalDateTime suggestedEnd) {
        this.suggestedEnd = suggestedEnd;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

