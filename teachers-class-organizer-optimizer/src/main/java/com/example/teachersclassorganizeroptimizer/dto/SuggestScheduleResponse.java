package com.example.teachersclassorganizeroptimizer.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class SuggestScheduleResponse {

    private Long teacherId;
    private Long schoolClassId;
    private Long subjectId;
    private Long roomId;
    private DayOfWeek suggestedDayOfWeek;
    private LocalTime suggestedStartTime;
    private LocalTime suggestedEndTime;
    private String message;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getSchoolClassId() {
        return schoolClassId;
    }

    public void setSchoolClassId(Long schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public DayOfWeek getSuggestedDayOfWeek() {
        return suggestedDayOfWeek;
    }

    public void setSuggestedDayOfWeek(DayOfWeek suggestedDayOfWeek) {
        this.suggestedDayOfWeek = suggestedDayOfWeek;
    }

    public LocalTime getSuggestedStartTime() {
        return suggestedStartTime;
    }

    public void setSuggestedStartTime(LocalTime suggestedStartTime) {
        this.suggestedStartTime = suggestedStartTime;
    }

    public LocalTime getSuggestedEndTime() {
        return suggestedEndTime;
    }

    public void setSuggestedEndTime(LocalTime suggestedEndTime) {
        this.suggestedEndTime = suggestedEndTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

