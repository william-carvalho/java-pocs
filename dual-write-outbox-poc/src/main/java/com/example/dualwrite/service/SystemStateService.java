package com.example.dualwrite.service;

import com.example.dualwrite.api.SystemStateResponse;
import com.example.dualwrite.broker.FakeMessageBroker;
import com.example.dualwrite.repository.OutboxEventRepository;
import com.example.dualwrite.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemStateService {

    private final UserAccountRepository userRepository;
    private final OutboxEventRepository outboxRepository;
    private final FakeMessageBroker broker;

    public SystemStateService(UserAccountRepository userRepository,
                              OutboxEventRepository outboxRepository,
                              FakeMessageBroker broker) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.broker = broker;
    }

    @Transactional(readOnly = true)
    public SystemStateResponse state() {
        return new SystemStateResponse(userRepository.findAllByOrderByCreatedAtAsc(),
                outboxRepository.findAllByOrderByCreatedAtAsc(), broker.getMessages(),
                broker.getDeliveryAttempts());
    }

    @Transactional
    public void reset() {
        outboxRepository.deleteAll();
        userRepository.deleteAll();
        broker.clear();
    }
}
