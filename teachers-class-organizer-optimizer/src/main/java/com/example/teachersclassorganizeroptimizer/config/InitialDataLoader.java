package com.example.teachersclassorganizeroptimizer.config;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadData(TeacherRepository teacherRepository,
                               SchoolClassRepository schoolClassRepository,
                               SubjectRepository subjectRepository,
                               RoomRepository roomRepository,
                               StudentRepository studentRepository,
                               ClassSessionRepository classSessionRepository) {
        return args -> {
            if (teacherRepository.count() > 0) {
                return;
            }

            Teacher maria = saveTeacher(teacherRepository, "Maria", "Math");
            Teacher joao = saveTeacher(teacherRepository, "João", "History");

            SchoolClass classA = saveClass(schoolClassRepository, "Class A", "5th Grade");
            SchoolClass classB = saveClass(schoolClassRepository, "Class B", "6th Grade");

            Subject math = saveSubject(subjectRepository, "Math");
            Subject science = saveSubject(subjectRepository, "Science");
            Subject history = saveSubject(subjectRepository, "History");

            Room room101 = saveRoom(roomRepository, "Room 101", 30);
            Room room102 = saveRoom(roomRepository, "Room 102", 40);

            saveStudent(studentRepository, "Ana", classA);
            saveStudent(studentRepository, "Bruno", classA);
            saveStudent(studentRepository, "Carla", classA);
            saveStudent(studentRepository, "Diego", classB);
            saveStudent(studentRepository, "Eva", classB);

            classSessionRepository.save(createSession(maria, classA, math, room101, DayOfWeek.MONDAY, "08:00", "09:00"));
            classSessionRepository.save(createSession(maria, classA, science, room101, DayOfWeek.MONDAY, "10:00", "11:00"));
            classSessionRepository.save(createSession(joao, classB, history, room102, DayOfWeek.WEDNESDAY, "09:00", "10:00"));
        };
    }

    private Teacher saveTeacher(TeacherRepository repository, String name, String specialty) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setSubjectSpecialty(specialty);
        return repository.save(teacher);
    }

    private SchoolClass saveClass(SchoolClassRepository repository, String name, String gradeLevel) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(name);
        schoolClass.setGradeLevel(gradeLevel);
        return repository.save(schoolClass);
    }

    private Subject saveSubject(SubjectRepository repository, String name) {
        Subject subject = new Subject();
        subject.setName(name);
        return repository.save(subject);
    }

    private Room saveRoom(RoomRepository repository, String name, Integer capacity) {
        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        return repository.save(room);
    }

    private void saveStudent(StudentRepository repository, String name, SchoolClass schoolClass) {
        Student student = new Student();
        student.setName(name);
        student.setSchoolClass(schoolClass);
        repository.save(student);
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
