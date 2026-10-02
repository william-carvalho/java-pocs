package com.example.dualwrite.service;

import com.example.dualwrite.domain.OutboxEvent;
import com.example.dualwrite.domain.UserAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class EventFactory {

    public static final String EVENT_TYPE = "UserRegistered";

    private final ObjectMapper objectMapper;
    private final Clock clock = Clock.systemUTC();

    public EventFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEvent userRegistered(UserAccount user) {
        return new OutboxEvent(UUID.randomUUID().toString(), user.getId(), EVENT_TYPE,
                payload(user), Instant.now(clock));
    }

    public String payload(UserAccount user) {
        Map<String, String> payload = new LinkedHashMap<String, String>();
        payload.put("userId", user.getId());
        payload.put("name", user.getName());
        payload.put("email", user.getEmail());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar o evento", exception);
        }
    }
}
