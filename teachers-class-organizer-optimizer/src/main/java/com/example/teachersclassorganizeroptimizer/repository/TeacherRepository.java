package com.example.teachersclassorganizeroptimizer.repository;

import com.example.teachersclassorganizeroptimizer.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}

