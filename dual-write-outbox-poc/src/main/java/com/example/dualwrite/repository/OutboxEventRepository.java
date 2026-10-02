package com.example.dualwrite.repository;

import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
    List<OutboxEvent> findAllByOrderByCreatedAtAsc();
}
