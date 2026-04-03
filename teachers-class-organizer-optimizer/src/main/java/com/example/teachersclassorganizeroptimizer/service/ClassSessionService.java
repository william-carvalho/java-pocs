package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.ClassSessionResponse;
import com.example.teachersclassorganizeroptimizer.dto.CreateClassSessionRequest;
import com.example.teachersclassorganizeroptimizer.entity.ClassSession;
import com.example.teachersclassorganizeroptimizer.entity.Room;
import com.example.teachersclassorganizeroptimizer.entity.SchoolClass;
import com.example.teachersclassorganizeroptimizer.entity.SessionStatus;
import com.example.teachersclassorganizeroptimizer.entity.Subject;
import com.example.teachersclassorganizeroptimizer.entity.Teacher;
import com.example.teachersclassorganizeroptimizer.exception.ConflictException;
import com.example.teachersclassorganizeroptimizer.exception.ResourceNotFoundException;
import com.example.teachersclassorganizeroptimizer.repository.ClassSessionRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;
    private final TeacherService teacherService;
    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;
    private final RoomService roomService;
    private final StudentService studentService;

    public ClassSessionService(ClassSessionRepository classSessionRepository,
                               TeacherService teacherService,
                               SchoolClassService schoolClassService,
                               SubjectService subjectService,
                               RoomService roomService,
                               StudentService studentService) {
        this.classSessionRepository = classSessionRepository;
        this.teacherService = teacherService;
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
        this.roomService = roomService;
        this.studentService = studentService;
    }

    @Transactional
    public ClassSessionResponse create(CreateClassSessionRequest request) {
        validateWindow(request.getStartTime(), request.getEndTime());

        Teacher teacher = teacherService.findEntity(request.getTeacherId());
        SchoolClass schoolClass = schoolClassService.findEntity(request.getSchoolClassId());
        Subject subject = subjectService.findEntity(request.getSubjectId());
        Room room = roomService.findEntity(request.getRoomId());

        validateRoomCapacity(schoolClass.getId(), room);
        validateNoConflict(teacher.getId(), schoolClass.getId(), room.getId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime());

        ClassSession session = new ClassSession();
        session.setTeacher(teacher);
        session.setSchoolClass(schoolClass);
        session.setSubject(subject);
        session.setRoom(room);
        session.setDayOfWeek(request.getDayOfWeek());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setStatus(SessionStatus.SCHEDULED);

        return toResponse(classSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<ClassSessionResponse> list(Long teacherId, Long schoolClassId, Long roomId, DayOfWeek dayOfWeek, SessionStatus status) {
        return classSessionRepository.search(teacherId, schoolClassId, roomId, dayOfWeek, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClassSessionResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public void cancel(Long id) {
        ClassSession session = findEntity(id);
        if (session.getStatus() != SessionStatus.CANCELLED) {
            session.setStatus(SessionStatus.CANCELLED);
            classSessionRepository.save(session);
        }
    }

    @Transactional(readOnly = true)
    public ClassSession findEntity(Long id) {
        return classSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<ClassSession> findScheduledByDay(DayOfWeek dayOfWeek) {
        return classSessionRepository.findScheduledByDay(dayOfWeek, SessionStatus.SCHEDULED);
    }

    @Transactional(readOnly = true)
    public boolean hasConflict(Long teacherId, Long schoolClassId, Long roomId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return findScheduledByDay(dayOfWeek).stream().anyMatch(existing ->
                overlaps(startTime, endTime, existing.getStartTime(), existing.getEndTime()) &&
                        (existing.getTeacher().getId().equals(teacherId)
                                || existing.getSchoolClass().getId().equals(schoolClassId)
                                || existing.getRoom().getId().equals(roomId))
        );
    }

    private void validateNoConflict(Long teacherId, Long schoolClassId, Long roomId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (hasConflict(teacherId, schoolClassId, roomId, dayOfWeek, startTime, endTime)) {
            throw new ConflictException("Teacher, class or room already has a session in the selected time range");
        }
    }

    private void validateRoomCapacity(Long schoolClassId, Room room) {
        long studentCount = studentService.countBySchoolClass(schoolClassId);
        if (studentCount > room.getCapacity()) {
            throw new ConflictException("Room capacity is not enough for the selected class");
        }
    }

    private void validateWindow(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be greater than startTime");
        }
    }

    private boolean overlaps(LocalTime newStart, LocalTime newEnd, LocalTime existingStart, LocalTime existingEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    public ClassSessionResponse toResponse(ClassSession session) {
        ClassSessionResponse response = new ClassSessionResponse();
        response.setId(session.getId());
        response.setTeacher(teacherService.toResponse(session.getTeacher()));
        response.setSchoolClass(schoolClassService.toResponse(session.getSchoolClass()));
        response.setSubject(subjectService.toResponse(session.getSubject()));
        response.setRoom(roomService.toResponse(session.getRoom()));
        response.setDayOfWeek(session.getDayOfWeek());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setStatus(session.getStatus());
        return response;
    }
}

