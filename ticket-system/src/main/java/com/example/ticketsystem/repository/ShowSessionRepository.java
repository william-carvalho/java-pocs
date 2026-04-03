package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.ShowSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShowSessionRepository extends JpaRepository<ShowSession, Long> {

    @EntityGraph(attributePaths = {"show", "venue"})
    List<ShowSession> findAllByOrderByEventDateTimeAscIdAsc();

    @Override
    @EntityGraph(attributePaths = {"show", "venue"})
    Optional<ShowSession> findById(Long id);
}
