package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.SchoolClassRequest;
import com.example.teachersclassorganizeroptimizer.dto.SchoolClassResponse;
import com.example.teachersclassorganizeroptimizer.entity.SchoolClass;
import com.example.teachersclassorganizeroptimizer.exception.ResourceNotFoundException;
import com.example.teachersclassorganizeroptimizer.repository.SchoolClassRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClassService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    @Transactional
    public SchoolClassResponse create(SchoolClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(request.getName().trim());
        schoolClass.setGradeLevel(request.getGradeLevel());
        return toResponse(schoolClassRepository.save(schoolClass));
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> listAll() {
        return schoolClassRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SchoolClass findEntity(Long id) {
        return schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School class not found with id " + id));
    }

    public SchoolClassResponse toResponse(SchoolClass schoolClass) {
        SchoolClassResponse response = new SchoolClassResponse();
        response.setId(schoolClass.getId());
        response.setName(schoolClass.getName());
        response.setGradeLevel(schoolClass.getGradeLevel());
        return response;
    }
}

