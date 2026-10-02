package com.example.dualwrite.service;

import com.example.dualwrite.api.RelayResult;
import com.example.dualwrite.broker.BrokerFailureMode;
import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.OutboxStatus;
import com.example.dualwrite.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxRelayService {

    private final OutboxEventRepository outboxRepository;
    private final OutboxEventProcessor processor;

    public OutboxRelayService(OutboxEventRepository outboxRepository, OutboxEventProcessor processor) {
        this.outboxRepository = outboxRepository;
        this.processor = processor;
    }

    public RelayResult relay(BrokerFailureMode failureMode) {
        List<OutboxEvent> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        int published = 0;
        int deduplicated = 0;
        int failed = 0;

        for (OutboxEvent event : pending) {
            ProcessResult result = processor.process(event.getId(), failureMode);
            if (result == ProcessResult.PUBLISHED) {
                published++;
            } else if (result == ProcessResult.DEDUPLICATED) {
                deduplicated++;
            } else {
                failed++;
            }
        }

        return new RelayResult(pending.size(), published, deduplicated, failed);
    }
}
