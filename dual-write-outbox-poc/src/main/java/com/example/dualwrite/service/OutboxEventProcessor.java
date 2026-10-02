package com.example.dualwrite.service;

import com.example.dualwrite.broker.BrokerFailureMode;
import com.example.dualwrite.broker.FakeMessageBroker;
import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OutboxEventProcessor {

    private final OutboxEventRepository outboxRepository;
    private final FakeMessageBroker broker;

    public OutboxEventProcessor(OutboxEventRepository outboxRepository, FakeMessageBroker broker) {
        this.outboxRepository = outboxRepository;
        this.broker = broker;
    }

    @Transactional
    public ProcessResult process(String eventId, BrokerFailureMode failureMode) {
        OutboxEvent event = outboxRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento Outbox nao encontrado: " + eventId));
        event.registerAttempt();

        try {
            boolean firstDelivery = broker.publish(event.getId(), event.getAggregateId(),
                    event.getEventType(), event.getPayload(), failureMode);
            event.markProcessed(Instant.now());
            return firstDelivery ? ProcessResult.PUBLISHED : ProcessResult.DEDUPLICATED;
        } catch (RuntimeException exception) {
            event.markFailed(exception.getMessage());
            return ProcessResult.FAILED;
        }
    }
}
