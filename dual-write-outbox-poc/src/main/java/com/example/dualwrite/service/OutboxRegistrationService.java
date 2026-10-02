package com.example.dualwrite.service;

import com.example.dualwrite.api.RegistrationRequest;
import com.example.dualwrite.api.RegistrationResult;
import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.UserAccount;
import com.example.dualwrite.repository.OutboxEventRepository;
import com.example.dualwrite.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxRegistrationService {

    private final UserAccountRepository userRepository;
    private final OutboxEventRepository outboxRepository;
    private final EventFactory eventFactory;

    public OutboxRegistrationService(UserAccountRepository userRepository,
                                     OutboxEventRepository outboxRepository,
                                     EventFactory eventFactory) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.eventFactory = eventFactory;
    }

    @Transactional
    public RegistrationResult register(RegistrationRequest request, boolean simulateRollback) {
        UserAccount user = new UserAccount(UUID.randomUUID().toString(), request.getName(),
                request.getEmail(), Instant.now());
        userRepository.save(user);

        OutboxEvent event = eventFactory.userRegistered(user);
        outboxRepository.save(event);
        outboxRepository.flush();

        if (simulateRollback) {
            throw new DemoFailureException(
                    "Rollback simulado: usuario e evento Outbox foram revertidos juntos");
        }

        return new RegistrationResult("TRANSACTIONAL_OUTBOX", user.getId(), event.getId(),
                "Usuario e evento foram persistidos atomicamente; execute o relay para publicar.");
    }
}
