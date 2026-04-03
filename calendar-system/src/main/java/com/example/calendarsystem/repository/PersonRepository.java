package com.example.calendarsystem.repository;

import com.example.calendarsystem.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}

