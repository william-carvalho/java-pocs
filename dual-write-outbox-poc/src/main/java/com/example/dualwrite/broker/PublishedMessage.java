package com.example.dualwrite.broker;

import java.time.Instant;

public class PublishedMessage {

    private final String eventId;
    private final String aggregateId;
    private final String eventType;
    private final String payload;
    private final Instant publishedAt;

    public PublishedMessage(String eventId, String aggregateId, String eventType, String payload, Instant publishedAt) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
