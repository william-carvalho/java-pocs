package com.example.dualwrite;

import com.example.dualwrite.api.RegistrationRequest;
import com.example.dualwrite.api.RelayResult;
import com.example.dualwrite.broker.BrokerFailureMode;
import com.example.dualwrite.broker.FakeMessageBroker;
import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.OutboxStatus;
import com.example.dualwrite.repository.OutboxEventRepository;
import com.example.dualwrite.repository.UserAccountRepository;
import com.example.dualwrite.service.DemoFailureException;
import com.example.dualwrite.service.NaiveRegistrationService;
import com.example.dualwrite.service.OutboxRegistrationService;
import com.example.dualwrite.service.OutboxRelayService;
import com.example.dualwrite.service.SystemStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DualWriteOutboxIntegrationTest {

    @Autowired
    private NaiveRegistrationService naiveService;

    @Autowired
    private OutboxRegistrationService outboxService;

    @Autowired
    private OutboxRelayService relayService;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private OutboxEventRepository outboxRepository;

    @Autowired
    private FakeMessageBroker broker;

    @Autowired
    private SystemStateService stateService;

    @BeforeEach
    void reset() {
        stateService.reset();
    }

    @Test
    void databaseFirstLeavesUserWithoutMessageWhenBrokerFails() {
        RegistrationRequest request = new RegistrationRequest("Ana", "ana@example.com");

        assertThatThrownBy(() -> naiveService.databaseFirst(request, true))
                .hasMessageContaining("antes da publicacao");

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(broker.getMessages()).isEmpty();
    }

    @Test
    void messageFirstLeavesGhostMessageWhenDatabaseRollsBack() {
        RegistrationRequest request = new RegistrationRequest("Bruno", "bruno@example.com");

        assertThatThrownBy(() -> naiveService.messageFirst(request, true))
                .isInstanceOf(DemoFailureException.class);

        assertThat(userRepository.count()).isZero();
        assertThat(broker.getMessages()).hasSize(1);
    }

    @Test
    void outboxRollsBackUserAndEventTogether() {
        RegistrationRequest request = new RegistrationRequest("Carla", "carla@example.com");

        assertThatThrownBy(() -> outboxService.register(request, true))
                .isInstanceOf(DemoFailureException.class);

        assertThat(userRepository.count()).isZero();
        assertThat(outboxRepository.count()).isZero();
        assertThat(broker.getMessages()).isEmpty();
    }

    @Test
    void outboxRetriesAfterBrokerComesBack() {
        outboxService.register(new RegistrationRequest("Diego", "diego@example.com"), false);

        RelayResult failed = relayService.relay(BrokerFailureMode.BEFORE_PUBLISH);
        assertThat(failed.getFailed()).isEqualTo(1);
        assertThat(broker.getMessages()).isEmpty();

        RelayResult retried = relayService.relay(BrokerFailureMode.NONE);
        OutboxEvent event = outboxRepository.findAll().get(0);
        assertThat(retried.getPublished()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PROCESSED);
        assertThat(event.getAttempts()).isEqualTo(2);
        assertThat(broker.getMessages()).hasSize(1);
    }

    @Test
    void eventIdDeduplicatesRetryWhenBrokerAckIsLost() {
        outboxService.register(new RegistrationRequest("Eva", "eva@example.com"), false);

        RelayResult ackLost = relayService.relay(BrokerFailureMode.AFTER_PUBLISH);
        assertThat(ackLost.getFailed()).isEqualTo(1);
        assertThat(broker.getMessages()).hasSize(1);

        RelayResult retried = relayService.relay(BrokerFailureMode.NONE);
        OutboxEvent event = outboxRepository.findAll().get(0);
        assertThat(retried.getDeduplicated()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PROCESSED);
        assertThat(event.getAttempts()).isEqualTo(2);
        assertThat(broker.getMessages()).hasSize(1);
        assertThat(broker.getDeliveryAttempts()).isEqualTo(2);
    }
}
