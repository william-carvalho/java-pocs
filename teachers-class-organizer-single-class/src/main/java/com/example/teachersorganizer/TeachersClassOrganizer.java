package com.example.teachersorganizer;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TeachersClassOrganizer {
    private final Map<String, Teacher> teachers = new LinkedHashMap<String, Teacher>();
    private final Map<String, SchoolClass> classes = new LinkedHashMap<String, SchoolClass>();
    private final Map<String, Subject> subjects = new LinkedHashMap<String, Subject>();
    private final Map<String, Room> rooms = new LinkedHashMap<String, Room>();
    private final List<ClassSession> sessions = new ArrayList<ClassSession>();
    private long nextSessionId = 1;

    public Teacher addTeacher(String name, String specialty) {
        Teacher teacher = new Teacher(name, specialty);
        teachers.put(teacher.getName(), teacher);
        return teacher;
    }

    public SchoolClass addClass(String name, String gradeLevel, int studentCount) {
        SchoolClass schoolClass = new SchoolClass(name, gradeLevel, studentCount);
        classes.put(schoolClass.getName(), schoolClass);
        return schoolClass;
    }

    public Subject addSubject(String name) {
        Subject subject = new Subject(name);
        subjects.put(subject.getName(), subject);
        return subject;
    }

    public Room addRoom(String name, int capacity) {
        Room room = new Room(name, capacity);
        rooms.put(room.getName(), room);
        return room;
    }

    public ClassSession schedule(String teacherName,
                                 String className,
                                 String subjectName,
                                 String roomName,
                                 DayOfWeek day,
                                 LocalTime start,
                                 LocalTime end) {
        Teacher teacher = findTeacher(teacherName);
        SchoolClass schoolClass = findClass(className);
        Subject subject = findSubject(subjectName);
        Room room = findRoom(roomName);
        validateTimeRange(start, end);
        validateRoomCapacity(room, schoolClass);
        validateNoConflicts(null, teacher, schoolClass, room, day, start, end);

        ClassSession session = new ClassSession(nextSessionId++, teacher, schoolClass, subject, room, day, start, end);
        sessions.add(session);
        return session;
    }

    public void cancelSession(long sessionId) {
        findSession(sessionId).cancel();
    }

    public SlotSuggestion suggest(String teacherName,
                                  String className,
                                  String subjectName,
                                  String roomName,
                                  int durationMinutes,
                                  List<DayOfWeek> preferredDays,
                                  LocalTime searchStart,
                                  LocalTime searchEnd) {
        Teacher teacher = findTeacher(teacherName);
        SchoolClass schoolClass = findClass(className);
        findSubject(subjectName);
        Room room = findRoom(roomName);

        if (durationMinutes <= 0 || durationMinutes % 30 != 0) {
            throw new IllegalArgumentException("durationMinutes must be a positive multiple of 30");
        }
        validateTimeRange(searchStart, searchEnd);
        validateRoomCapacity(room, schoolClass);

        List<DayOfWeek> days = preferredDays == null || preferredDays.isEmpty()
                ? Arrays.asList(DayOfWeek.values())
                : preferredDays;

        SlotSuggestion best = null;
        int bestScore = Integer.MIN_VALUE;
        for (DayOfWeek day : days) {
            LocalTime start = searchStart;
            while (!start.plusMinutes(durationMinutes).isAfter(searchEnd)) {
                LocalTime end = start.plusMinutes(durationMinutes);
                if (isAvailable(teacher, schoolClass, room, day, start, end)) {
                    int score = adjacencyScore(teacher, day, start, end);
                    if (best == null || score > bestScore) {
                        best = new SlotSuggestion(day, start, end, score > 0
                                ? "Optimized slot adjacent to teacher schedule"
                                : "First valid slot found");
                        bestScore = score;
                    }
                }
                start = start.plusMinutes(30);
            }
        }

        if (best == null) {
            throw new IllegalStateException("No available slot found");
        }
        return best;
    }

    public List<ClassSession> sessions() {
        return Collections.unmodifiableList(sessions);
    }

    public List<ClassSession> activeSessions() {
        List<ClassSession> active = new ArrayList<ClassSession>();
        for (ClassSession session : sessions) {
            if (session.getStatus() == SessionStatus.SCHEDULED) {
                active.add(session);
            }
        }
        return Collections.unmodifiableList(active);
    }

    public static TeachersClassOrganizer withDefaultData() {
        TeachersClassOrganizer organizer = new TeachersClassOrganizer();
        organizer.addTeacher("Maria", "Math");
        organizer.addTeacher("Joao", "History");
        organizer.addClass("Class A", "5th Grade", 28);
        organizer.addClass("Class B", "6th Grade", 35);
        organizer.addSubject("Math");
        organizer.addSubject("Science");
        organizer.addSubject("History");
        organizer.addRoom("Room 101", 30);
        organizer.addRoom("Room 102", 40);
        organizer.schedule("Maria", "Class A", "Math", "Room 101", DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));
        organizer.schedule("Maria", "Class A", "Science", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        organizer.schedule("Joao", "Class B", "History", "Room 102", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        return organizer;
    }

    private boolean isAvailable(Teacher teacher, SchoolClass schoolClass, Room room, DayOfWeek day, LocalTime start, LocalTime end) {
        try {
            validateNoConflicts(null, teacher, schoolClass, room, day, start, end);
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    private int adjacencyScore(Teacher teacher, DayOfWeek day, LocalTime start, LocalTime end) {
        int score = 0;
        for (ClassSession session : activeSessions()) {
            if (session.getTeacher().equals(teacher) && session.getDay().equals(day)) {
                if (session.getEnd().equals(start) || session.getStart().equals(end)) {
                    score += 10;
                }
            }
        }
        return score;
    }

    private void validateNoConflicts(Long ignoredSessionId,
                                     Teacher teacher,
                                     SchoolClass schoolClass,
                                     Room room,
                                     DayOfWeek day,
                                     LocalTime start,
                                     LocalTime end) {
        for (ClassSession session : activeSessions()) {
            if (ignoredSessionId != null && session.getId() == ignoredSessionId) {
                continue;
            }
            if (!session.getDay().equals(day) || !overlaps(start, end, session.getStart(), session.getEnd())) {
                continue;
            }
            if (session.getTeacher().equals(teacher)) {
                throw new IllegalStateException("Teacher has a conflict");
            }
            if (session.getSchoolClass().equals(schoolClass)) {
                throw new IllegalStateException("Class has a conflict");
            }
            if (session.getRoom().equals(room)) {
                throw new IllegalStateException("Room has a conflict");
            }
        }
    }

    private static boolean overlaps(LocalTime start, LocalTime end, LocalTime otherStart, LocalTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
    }

    private static void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }
    }

    private static void validateRoomCapacity(Room room, SchoolClass schoolClass) {
        if (room.getCapacity() < schoolClass.getStudentCount()) {
            throw new IllegalStateException("Room capacity is lower than class size");
        }
    }

    private Teacher findTeacher(String name) {
        Teacher teacher = teachers.get(trim(name));
        if (teacher == null) {
            throw new IllegalArgumentException("Unknown teacher: " + name);
        }
        return teacher;
    }

    private SchoolClass findClass(String name) {
        SchoolClass schoolClass = classes.get(trim(name));
        if (schoolClass == null) {
            throw new IllegalArgumentException("Unknown class: " + name);
        }
        return schoolClass;
    }

    private Subject findSubject(String name) {
        Subject subject = subjects.get(trim(name));
        if (subject == null) {
            throw new IllegalArgumentException("Unknown subject: " + name);
        }
        return subject;
    }

    private Room findRoom(String name) {
        Room room = rooms.get(trim(name));
        if (room == null) {
            throw new IllegalArgumentException("Unknown room: " + name);
        }
        return room;
    }

    private ClassSession findSession(long id) {
        for (ClassSession session : sessions) {
            if (session.getId() == id) {
                return session;
            }
        }
        throw new IllegalArgumentException("Unknown session: " + id);
    }

    private static String trim(String text) {
        return text == null ? null : text.trim();
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public enum SessionStatus {
        SCHEDULED,
        CANCELLED
    }

    public static final class Teacher {
        private final String name;
        private final String specialty;

        private Teacher(String name, String specialty) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("teacher name is required");
            }
            this.name = name.trim();
            this.specialty = specialty == null ? "" : specialty.trim();
        }

        public String getName() {
            return name;
        }

        public String getSpecialty() {
            return specialty;
        }
    }

    public static final class SchoolClass {
        private final String name;
        private final String gradeLevel;
        private final int studentCount;

        private SchoolClass(String name, String gradeLevel, int studentCount) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("class name is required");
            }
            if (studentCount <= 0) {
                throw new IllegalArgumentException("studentCount must be greater than zero");
            }
            this.name = name.trim();
            this.gradeLevel = gradeLevel == null ? "" : gradeLevel.trim();
            this.studentCount = studentCount;
        }

        public String getName() {
            return name;
        }

        public String getGradeLevel() {
            return gradeLevel;
        }

        public int getStudentCount() {
            return studentCount;
        }
    }

    public static final class Subject {
        private final String name;

        private Subject(String name) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("subject name is required");
            }
            this.name = name.trim();
        }

        public String getName() {
            return name;
        }
    }

    public static final class Room {
        private final String name;
        private final int capacity;

        private Room(String name, int capacity) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("room name is required");
            }
            if (capacity <= 0) {
                throw new IllegalArgumentException("capacity must be greater than zero");
            }
            this.name = name.trim();
            this.capacity = capacity;
        }

        public String getName() {
            return name;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static final class ClassSession {
        private final long id;
        private final Teacher teacher;
        private final SchoolClass schoolClass;
        private final Subject subject;
        private final Room room;
        private final DayOfWeek day;
        private final LocalTime start;
        private final LocalTime end;
        private SessionStatus status = SessionStatus.SCHEDULED;

        private ClassSession(long id, Teacher teacher, SchoolClass schoolClass, Subject subject, Room room, DayOfWeek day, LocalTime start, LocalTime end) {
            this.id = id;
            this.teacher = teacher;
            this.schoolClass = schoolClass;
            this.subject = subject;
            this.room = room;
            this.day = day;
            this.start = start;
            this.end = end;
        }

        private void cancel() {
            status = SessionStatus.CANCELLED;
        }

        public long getId() {
            return id;
        }

        public Teacher getTeacher() {
            return teacher;
        }

        public SchoolClass getSchoolClass() {
            return schoolClass;
        }

        public Subject getSubject() {
            return subject;
        }

        public Room getRoom() {
            return room;
        }

        public DayOfWeek getDay() {
            return day;
        }

        public LocalTime getStart() {
            return start;
        }

        public LocalTime getEnd() {
            return end;
        }

        public SessionStatus getStatus() {
            return status;
        }
    }

    public static final class SlotSuggestion {
        private final DayOfWeek day;
        private final LocalTime start;
        private final LocalTime end;
        private final String message;

        private SlotSuggestion(DayOfWeek day, LocalTime start, LocalTime end, String message) {
            this.day = day;
            this.start = start;
            this.end = end;
            this.message = message;
        }

        public DayOfWeek getDay() {
            return day;
        }

        public LocalTime getStart() {
            return start;
        }

        public LocalTime getEnd() {
            return end;
        }

        public String getMessage() {
            return message;
        }
    }
}
