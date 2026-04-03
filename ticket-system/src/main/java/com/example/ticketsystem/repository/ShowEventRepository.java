package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.ShowEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowEventRepository extends JpaRepository<ShowEvent, Long> {

    boolean existsByNameIgnoreCase(String name);
}
