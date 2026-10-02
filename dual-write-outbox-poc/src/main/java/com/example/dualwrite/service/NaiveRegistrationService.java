package com.example.dualwrite.service;

import com.example.dualwrite.api.RegistrationRequest;
import com.example.dualwrite.api.RegistrationResult;
import com.example.dualwrite.broker.BrokerFailureMode;
import com.example.dualwrite.broker.FakeMessageBroker;
import com.example.dualwrite.domain.UserAccount;
import com.example.dualwrite.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class NaiveRegistrationService {

    private final UserPersistenceService userPersistenceService;
    private final UserAccountRepository userRepository;
    private final FakeMessageBroker broker;
    private final EventFactory eventFactory;

    public NaiveRegistrationService(UserPersistenceService userPersistenceService,
                                    UserAccountRepository userRepository,
                                    FakeMessageBroker broker,
                                    EventFactory eventFactory) {
        this.userPersistenceService = userPersistenceService;
        this.userRepository = userRepository;
        this.broker = broker;
        this.eventFactory = eventFactory;
    }

    public RegistrationResult databaseFirst(RegistrationRequest request, boolean simulateBrokerFailure) {
        UserAccount user = userPersistenceService.createAndCommit(request);
        String eventId = UUID.randomUUID().toString();
        BrokerFailureMode failure = simulateBrokerFailure
                ? BrokerFailureMode.BEFORE_PUBLISH : BrokerFailureMode.NONE;
        broker.publish(eventId, user.getId(), EventFactory.EVENT_TYPE, eventFactory.payload(user), failure);
        return new RegistrationResult("NAIVE_DATABASE_FIRST", user.getId(), eventId,
                "O usuario foi commitado antes da tentativa de publicar a mensagem.");
    }

    @Transactional
    public RegistrationResult messageFirst(RegistrationRequest request, boolean simulateDatabaseFailure) {
        UserAccount user = new UserAccount(UUID.randomUUID().toString(), request.getName(),
                request.getEmail(), Instant.now());
        String eventId = UUID.randomUUID().toString();

        broker.publish(eventId, user.getId(), EventFactory.EVENT_TYPE,
                eventFactory.payload(user), BrokerFailureMode.NONE);
        userRepository.saveAndFlush(user);

        if (simulateDatabaseFailure) {
            throw new DemoFailureException(
                    "Falha de banco simulada: a transacao foi revertida, mas a mensagem ja saiu");
        }

        return new RegistrationResult("NAIVE_MESSAGE_FIRST", user.getId(), eventId,
                "A mensagem foi publicada antes do commit do usuario.");
    }
}
