package com.example.hibernateslowquerydetector.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hibernateslowquerydetector.entity.SlowQueryRecord;

public interface SlowQueryRecordRepository extends JpaRepository<SlowQueryRecord, Long> {

    List<SlowQueryRecord> findAllByOrderByDetectedAtDesc();
}

