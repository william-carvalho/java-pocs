package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.StudentRequest;
import com.example.teachersclassorganizeroptimizer.dto.StudentResponse;
import com.example.teachersclassorganizeroptimizer.entity.Student;
import com.example.teachersclassorganizeroptimizer.repository.StudentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassService schoolClassService;

    public StudentService(StudentRepository studentRepository, SchoolClassService schoolClassService) {
        this.studentRepository = studentRepository;
        this.schoolClassService = schoolClassService;
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName().trim());
        student.setSchoolClass(schoolClassService.findEntity(request.getSchoolClassId()));
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> listAll(Long schoolClassId) {
        List<Student> students = schoolClassId == null
                ? studentRepository.findAll()
                : studentRepository.findBySchoolClassId(schoolClassId);
        return students.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countBySchoolClass(Long schoolClassId) {
        return studentRepository.countBySchoolClassId(schoolClassId);
    }

    public StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setSchoolClass(schoolClassService.toResponse(student.getSchoolClass()));
        return response;
    }
}

