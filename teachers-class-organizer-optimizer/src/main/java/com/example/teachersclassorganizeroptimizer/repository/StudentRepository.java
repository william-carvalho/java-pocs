package com.example.teachersclassorganizeroptimizer.repository;

import com.example.teachersclassorganizeroptimizer.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchoolClassId(Long schoolClassId);
    long countBySchoolClassId(Long schoolClassId);
}

