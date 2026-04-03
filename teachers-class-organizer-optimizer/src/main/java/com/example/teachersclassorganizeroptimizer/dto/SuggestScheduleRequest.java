package com.example.teachersclassorganizeroptimizer.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SuggestScheduleRequest {

    @NotNull(message = "teacherId is required")
    private Long teacherId;

    @NotNull(message = "schoolClassId is required")
    private Long schoolClassId;

    @NotNull(message = "subjectId is required")
    private Long subjectId;

    private Long roomId;

    @NotNull(message = "durationMinutes is required")
    @Min(value = 1, message = "durationMinutes must be greater than zero")
    private Integer durationMinutes;

    @NotEmpty(message = "preferredDays is required")
    private List<DayOfWeek> preferredDays;

    @NotNull(message = "searchStartTime is required")
    private LocalTime searchStartTime;

    @NotNull(message = "searchEndTime is required")
    private LocalTime searchEndTime;

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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<DayOfWeek> getPreferredDays() {
        return preferredDays;
    }

    public void setPreferredDays(List<DayOfWeek> preferredDays) {
        this.preferredDays = preferredDays;
    }

    public LocalTime getSearchStartTime() {
        return searchStartTime;
    }

    public void setSearchStartTime(LocalTime searchStartTime) {
        this.searchStartTime = searchStartTime;
    }

    public LocalTime getSearchEndTime() {
        return searchEndTime;
    }

    public void setSearchEndTime(LocalTime searchEndTime) {
        this.searchEndTime = searchEndTime;
    }
}

