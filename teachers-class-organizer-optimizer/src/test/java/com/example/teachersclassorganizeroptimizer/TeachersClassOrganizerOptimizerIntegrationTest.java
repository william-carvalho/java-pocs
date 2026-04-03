package com.example.teachersclassorganizeroptimizer;

import com.example.teachersclassorganizeroptimizer.entity.ClassSession;
import com.example.teachersclassorganizeroptimizer.entity.Room;
import com.example.teachersclassorganizeroptimizer.entity.SchoolClass;
import com.example.teachersclassorganizeroptimizer.entity.SessionStatus;
import com.example.teachersclassorganizeroptimizer.entity.Student;
import com.example.teachersclassorganizeroptimizer.entity.Subject;
import com.example.teachersclassorganizeroptimizer.entity.Teacher;
import com.example.teachersclassorganizeroptimizer.repository.ClassSessionRepository;
import com.example.teachersclassorganizeroptimizer.repository.RoomRepository;
import com.example.teachersclassorganizeroptimizer.repository.SchoolClassRepository;
import com.example.teachersclassorganizeroptimizer.repository.StudentRepository;
import com.example.teachersclassorganizeroptimizer.repository.SubjectRepository;
import com.example.teachersclassorganizeroptimizer.repository.TeacherRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TeachersClassOrganizerOptimizerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @BeforeEach
    void setUp() {
        classSessionRepository.deleteAll();
        studentRepository.deleteAll();
        roomRepository.deleteAll();
        subjectRepository.deleteAll();
        schoolClassRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void shouldCreateListAndCancelSession() throws Exception {
        Teacher teacher = saveTeacher("Maria");
        SchoolClass schoolClass = saveClass("Class A");
        Subject subject = saveSubject("Math");
        Room room = saveRoom("Room 101", 30);
        saveStudent("Ana", schoolClass);
        saveStudent("Bruno", schoolClass);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teacherId\":" + teacher.getId() + ",\"schoolClassId\":" + schoolClass.getId() + ",\"subjectId\":" + subject.getId() + ",\"roomId\":" + room.getId() + ",\"dayOfWeek\":\"MONDAY\",\"startTime\":\"08:00\",\"endTime\":\"09:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacher.name", is("Maria")))
                .andExpect(jsonPath("$.schoolClass.name", is("Class A")))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));

        Long sessionId = classSessionRepository.findAll().get(0).getId();

        mockMvc.perform(get("/sessions").param("teacherId", teacher.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/sessions/{id}", sessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void shouldRejectConflictAndInsufficientRoomCapacity() throws Exception {
        Teacher teacher = saveTeacher("Maria");
        SchoolClass schoolClass = saveClass("Class A");
        Subject subject = saveSubject("Math");
        Room smallRoom = saveRoom("Room 101", 1);
        Room room = saveRoom("Room 102", 30);
        saveStudent("Ana", schoolClass);
        saveStudent("Bruno", schoolClass);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teacherId\":" + teacher.getId() + ",\"schoolClassId\":" + schoolClass.getId() + ",\"subjectId\":" + subject.getId() + ",\"roomId\":" + smallRoom.getId() + ",\"dayOfWeek\":\"MONDAY\",\"startTime\":\"08:00\",\"endTime\":\"09:00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Room capacity is not enough for the selected class")));

        classSessionRepository.save(createSession(teacher, schoolClass, subject, room, DayOfWeek.MONDAY, "08:00", "09:00"));

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teacherId\":" + teacher.getId() + ",\"schoolClassId\":" + schoolClass.getId() + ",\"subjectId\":" + subject.getId() + ",\"roomId\":" + room.getId() + ",\"dayOfWeek\":\"MONDAY\",\"startTime\":\"08:30\",\"endTime\":\"09:30\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Teacher, class or room already has a session in the selected time range")));
    }

    @Test
    void shouldSuggestFirstValidOptimizedSlot() throws Exception {
        Teacher teacher = saveTeacher("Maria");
        SchoolClass schoolClass = saveClass("Class A");
        Subject subject = saveSubject("Math");
        Room room = saveRoom("Room 101", 30);
        saveStudent("Ana", schoolClass);
        saveStudent("Bruno", schoolClass);

        classSessionRepository.save(createSession(teacher, schoolClass, subject, room, DayOfWeek.MONDAY, "08:00", "09:00"));
        classSessionRepository.save(createSession(teacher, schoolClass, subject, room, DayOfWeek.MONDAY, "10:00", "11:00"));

        mockMvc.perform(post("/sessions/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teacherId\":" + teacher.getId() + ",\"schoolClassId\":" + schoolClass.getId() + ",\"subjectId\":" + subject.getId() + ",\"roomId\":" + room.getId() + ",\"durationMinutes\":60,\"preferredDays\":[\"MONDAY\",\"WEDNESDAY\"],\"searchStartTime\":\"08:00\",\"searchEndTime\":\"18:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedDayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.suggestedStartTime", is("09:00:00")))
                .andExpect(jsonPath("$.suggestedEndTime", is("10:00:00")))
                .andExpect(jsonPath("$.message", is("First valid optimized slot found")));
    }

    private Teacher saveTeacher(String name) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        return teacherRepository.save(teacher);
    }

    private SchoolClass saveClass(String name) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(name);
        return schoolClassRepository.save(schoolClass);
    }

    private Subject saveSubject(String name) {
        Subject subject = new Subject();
        subject.setName(name);
        return subjectRepository.save(subject);
    }

    private Room saveRoom(String name, int capacity) {
        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        return roomRepository.save(room);
    }

    private void saveStudent(String name, SchoolClass schoolClass) {
        Student student = new Student();
        student.setName(name);
        student.setSchoolClass(schoolClass);
        studentRepository.save(student);
    }

    private ClassSession createSession(Teacher teacher, SchoolClass schoolClass, Subject subject, Room room,
                                       DayOfWeek dayOfWeek, String startTime, String endTime) {
        ClassSession session = new ClassSession();
        session.setTeacher(teacher);
        session.setSchoolClass(schoolClass);
        session.setSubject(subject);
        session.setRoom(room);
        session.setDayOfWeek(dayOfWeek);
        session.setStartTime(LocalTime.parse(startTime));
        session.setEndTime(LocalTime.parse(endTime));
        session.setStatus(SessionStatus.SCHEDULED);
        return session;
    }
}

