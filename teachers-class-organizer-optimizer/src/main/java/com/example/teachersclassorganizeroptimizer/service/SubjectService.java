package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.SubjectRequest;
import com.example.teachersclassorganizeroptimizer.dto.SubjectResponse;
import com.example.teachersclassorganizeroptimizer.entity.Subject;
import com.example.teachersclassorganizeroptimizer.exception.ResourceNotFoundException;
import com.example.teachersclassorganizeroptimizer.repository.SubjectRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        Subject subject = new Subject();
        subject.setName(request.getName().trim());
        return toResponse(subjectRepository.save(subject));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> listAll() {
        return subjectRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Subject findEntity(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + id));
    }

    public SubjectResponse toResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        return response;
    }
}

