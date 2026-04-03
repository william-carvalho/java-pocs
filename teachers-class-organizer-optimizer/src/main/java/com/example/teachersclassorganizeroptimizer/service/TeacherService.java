package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.TeacherRequest;
import com.example.teachersclassorganizeroptimizer.dto.TeacherResponse;
import com.example.teachersclassorganizeroptimizer.entity.Teacher;
import com.example.teachersclassorganizeroptimizer.exception.ResourceNotFoundException;
import com.example.teachersclassorganizeroptimizer.repository.TeacherRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        Teacher teacher = new Teacher();
        teacher.setName(request.getName().trim());
        teacher.setSubjectSpecialty(request.getSubjectSpecialty());
        return toResponse(teacherRepository.save(teacher));
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> listAll() {
        return teacherRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Teacher findEntity(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id " + id));
    }

    public TeacherResponse toResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setName(teacher.getName());
        response.setSubjectSpecialty(teacher.getSubjectSpecialty());
        return response;
    }
}

