package com.example.dontpadclone.repository;

import com.example.dontpadclone.entity.Pad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PadRepository extends JpaRepository<Pad, Long> {

    Optional<Pad> findBySlug(String slug);

    void deleteBySlug(String slug);

    boolean existsBySlug(String slug);
}

