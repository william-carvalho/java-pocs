package com.example.dualwrite.api;

public class RegistrationResult {

    private final String flow;
    private final String userId;
    private final String eventId;
    private final String explanation;

    public RegistrationResult(String flow, String userId, String eventId, String explanation) {
        this.flow = flow;
        this.userId = userId;
        this.eventId = eventId;
        this.explanation = explanation;
    }

    public String getFlow() {
        return flow;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getExplanation() {
        return explanation;
    }
}
