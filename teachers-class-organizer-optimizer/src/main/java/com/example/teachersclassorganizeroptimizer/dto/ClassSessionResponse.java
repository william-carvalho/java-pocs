package com.example.teachersclassorganizeroptimizer.dto;

import com.example.teachersclassorganizeroptimizer.entity.SessionStatus;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class ClassSessionResponse {

    private Long id;
    private TeacherResponse teacher;
    private SchoolClassResponse schoolClass;
    private SubjectResponse subject;
    private RoomResponse room;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeacherResponse getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherResponse teacher) {
        this.teacher = teacher;
    }

    public SchoolClassResponse getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClassResponse schoolClass) {
        this.schoolClass = schoolClass;
    }

    public SubjectResponse getSubject() {
        return subject;
    }

    public void setSubject(SubjectResponse subject) {
        this.subject = subject;
    }

    public RoomResponse getRoom() {
        return room;
    }

    public void setRoom(RoomResponse room) {
        this.room = room;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}

