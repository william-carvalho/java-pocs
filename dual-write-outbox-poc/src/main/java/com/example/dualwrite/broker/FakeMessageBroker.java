package com.example.dualwrite.broker;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FakeMessageBroker {

    private final Map<String, PublishedMessage> messages = new ConcurrentHashMap<String, PublishedMessage>();
    private final AtomicLong deliveryAttempts = new AtomicLong();
    private final Clock clock;

    public FakeMessageBroker() {
        this(Clock.systemUTC());
    }

    FakeMessageBroker(Clock clock) {
        this.clock = clock;
    }

    public boolean publish(String eventId, String aggregateId, String eventType, String payload,
                           BrokerFailureMode failureMode) {
        deliveryAttempts.incrementAndGet();

        if (failureMode == BrokerFailureMode.BEFORE_PUBLISH) {
            throw new BrokerUnavailableException("Broker indisponivel antes da publicacao");
        }

        PublishedMessage message = new PublishedMessage(
                eventId, aggregateId, eventType, payload, Instant.now(clock));
        boolean firstDelivery = messages.putIfAbsent(eventId, message) == null;

        if (failureMode == BrokerFailureMode.AFTER_PUBLISH) {
            throw new BrokerUnavailableException(
                    "ACK perdido: a mensagem foi publicada, mas o relay nao recebeu a confirmacao");
        }

        return firstDelivery;
    }

    public List<PublishedMessage> getMessages() {
        List<PublishedMessage> result = new ArrayList<PublishedMessage>(messages.values());
        result.sort(Comparator.comparing(PublishedMessage::getPublishedAt));
        return result;
    }

    public long getDeliveryAttempts() {
        return deliveryAttempts.get();
    }

    public void clear() {
        messages.clear();
        deliveryAttempts.set(0);
    }
}
