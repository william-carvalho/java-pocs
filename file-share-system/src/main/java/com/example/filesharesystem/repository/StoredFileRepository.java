package com.example.filesharesystem.repository;

import com.example.filesharesystem.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByDeletedFalseOrderByCreatedAtDesc();

    List<StoredFile> findByDeletedFalseAndOriginalFileNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    Optional<StoredFile> findByIdAndDeletedFalse(Long id);
}

