package com.example.teachersclassorganizeroptimizer.repository;

import com.example.teachersclassorganizeroptimizer.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}

