package com.example.dualwrite.api;

import com.example.dualwrite.broker.PublishedMessage;
import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.UserAccount;

import java.util.List;

public class SystemStateResponse {

    private final List<UserAccount> users;
    private final List<OutboxEvent> outboxEvents;
    private final List<PublishedMessage> brokerMessages;
    private final long brokerDeliveryAttempts;

    public SystemStateResponse(List<UserAccount> users, List<OutboxEvent> outboxEvents,
                               List<PublishedMessage> brokerMessages, long brokerDeliveryAttempts) {
        this.users = users;
        this.outboxEvents = outboxEvents;
        this.brokerMessages = brokerMessages;
        this.brokerDeliveryAttempts = brokerDeliveryAttempts;
    }

    public List<UserAccount> getUsers() {
        return users;
    }

    public List<OutboxEvent> getOutboxEvents() {
        return outboxEvents;
    }

    public List<PublishedMessage> getBrokerMessages() {
        return brokerMessages;
    }

    public long getBrokerDeliveryAttempts() {
        return brokerDeliveryAttempts;
    }
}
