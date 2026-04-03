package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.SuggestScheduleRequest;
import com.example.teachersclassorganizeroptimizer.dto.SuggestScheduleResponse;
import com.example.teachersclassorganizeroptimizer.entity.ClassSession;
import com.example.teachersclassorganizeroptimizer.entity.Room;
import com.example.teachersclassorganizeroptimizer.exception.ConflictException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionSuggestionService {

    private static final int SLOT_MINUTES = 30;

    private final TeacherService teacherService;
    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;
    private final RoomService roomService;
    private final StudentService studentService;
    private final ClassSessionService classSessionService;

    public SessionSuggestionService(TeacherService teacherService,
                                    SchoolClassService schoolClassService,
                                    SubjectService subjectService,
                                    RoomService roomService,
                                    StudentService studentService,
                                    ClassSessionService classSessionService) {
        this.teacherService = teacherService;
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
        this.roomService = roomService;
        this.studentService = studentService;
        this.classSessionService = classSessionService;
    }

    @Transactional(readOnly = true)
    public SuggestScheduleResponse suggest(SuggestScheduleRequest request) {
        teacherService.findEntity(request.getTeacherId());
        schoolClassService.findEntity(request.getSchoolClassId());
        subjectService.findEntity(request.getSubjectId());

        validateRequest(request);

        long studentCount = studentService.countBySchoolClass(request.getSchoolClassId());
        List<Room> candidateRooms = request.getRoomId() != null
                ? java.util.Collections.singletonList(roomService.findEntity(request.getRoomId()))
                : roomService.findCandidateRooms(studentCount);

        if (candidateRooms.isEmpty()) {
            throw new ConflictException("No room supports the selected class size");
        }

        for (DayOfWeek day : request.getPreferredDays()) {
            List<ClassSession> teacherDaySessions = classSessionService.findScheduledByDay(day).stream()
                    .filter(session -> session.getTeacher().getId().equals(request.getTeacherId()))
                    .sorted(Comparator.comparing(ClassSession::getStartTime))
                    .collect(java.util.stream.Collectors.toList());

            Optional<SuggestScheduleResponse> suggestion = suggestForDay(request, candidateRooms, teacherDaySessions, day, studentCount);
            if (suggestion.isPresent()) {
                return suggestion.get();
            }
        }

        SuggestScheduleResponse response = baseResponse(request);
        response.setMessage("No valid slot found in the informed window");
        return response;
    }

    private Optional<SuggestScheduleResponse> suggestForDay(SuggestScheduleRequest request,
                                                            List<Room> candidateRooms,
                                                            List<ClassSession> teacherDaySessions,
                                                            DayOfWeek day,
                                                            long studentCount) {
        LocalTime start = request.getSearchStartTime();
        while (!start.plusMinutes(request.getDurationMinutes()).isAfter(request.getSearchEndTime())) {
            LocalTime end = start.plusMinutes(request.getDurationMinutes());

            for (Room room : candidateRooms) {
                if (room.getCapacity() < studentCount) {
                    continue;
                }
                if (!classSessionService.hasConflict(request.getTeacherId(), request.getSchoolClassId(), room.getId(), day, start, end)) {
                    SuggestScheduleResponse response = baseResponse(request);
                    response.setRoomId(room.getId());
                    response.setSuggestedDayOfWeek(day);
                    response.setSuggestedStartTime(start);
                    response.setSuggestedEndTime(end);
                    response.setMessage("First valid optimized slot found");
                    return Optional.of(response);
                }
            }

            start = start.plusMinutes(SLOT_MINUTES);
        }
        return Optional.empty();
    }

    private void validateRequest(SuggestScheduleRequest request) {
        if (!request.getSearchEndTime().isAfter(request.getSearchStartTime())) {
            throw new IllegalArgumentException("searchEndTime must be greater than searchStartTime");
        }
        if (request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than zero");
        }
    }

    private SuggestScheduleResponse baseResponse(SuggestScheduleRequest request) {
        SuggestScheduleResponse response = new SuggestScheduleResponse();
        response.setTeacherId(request.getTeacherId());
        response.setSchoolClassId(request.getSchoolClassId());
        response.setSubjectId(request.getSubjectId());
        response.setRoomId(request.getRoomId());
        return response;
    }
}
