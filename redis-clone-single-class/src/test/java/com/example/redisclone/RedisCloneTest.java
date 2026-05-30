package com.example.redisclone;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedisCloneTest {
    @Test
    void clientCanSetAndGetStringsThroughServer() {
        RedisClone.Server server = RedisClone.server();
        RedisClone.Client client = RedisClone.client(server);

        assertEquals("OK", client.set("city", "Floripa"));

        assertEquals("Floripa", client.get("city"));
    }

    @Test
    void clientCanRemoveStrings() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());
        client.set("token", "abc");

        assertEquals("abc", client.remove("token"));

        assertNull(client.get("token"));
    }

    @Test
    void appendUpdatesExistingStringAndCreatesMissingKey() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        client.set("city", "Flori");

        assertEquals("Floripa", client.append("city", "pa"));
        assertEquals("SC", client.append("state", "SC"));
        assertEquals("SC", client.get("state"));
    }

    @Test
    void clientCanSetAndGetMapValuesThroughServer() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        assertEquals("OK", client.mapSet("person", "name", "William"));
        assertEquals("OK", client.mapSet("person", "role", "CTO"));

        assertEquals("William", client.mapGet("person", "name"));
        assertEquals("CTO", client.mapGet("person", "role"));
    }

    @Test
    void mapKeysAndValuesPreserveInsertionOrder() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        client.mapSet("person", "name", "William");
        client.mapSet("person", "role", "CTO");

        assertEquals(Arrays.asList("name", "role"), client.mapKeys("person"));
        assertEquals(Arrays.asList("William", "CTO"), client.mapValues("person"));
    }

    @Test
    void missingMapReturnsEmptyKeysAndValues() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        assertEquals(Collections.emptyList(), client.mapKeys("missing"));
        assertEquals(Collections.emptyList(), client.mapValues("missing"));
    }

    @Test
    void twoClientsShareTheSameServerState() {
        RedisClone.Server server = RedisClone.server();
        RedisClone.Client first = RedisClone.client(server);
        RedisClone.Client second = RedisClone.client(server);

        first.set("shared", "hello");
        first.mapSet("session", "user", "Ana");

        assertEquals("hello", second.get("shared"));
        assertEquals("Ana", second.mapGet("session", "user"));
    }

    @Test
    void supportsTextProtocolCommands() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        assertEquals("OK", client.execute("SET city Floripa").asProtocolLine());
        assertEquals("VALUE Floripa", client.execute("GET city").asProtocolLine());
        assertEquals("VALUE FloripaSC", client.execute("APPEND city SC").asProtocolLine());
        assertEquals("OK", client.execute("MAP_SET person name William").asProtocolLine());
        assertEquals("VALUE William", client.execute("MAP_GET person name").asProtocolLine());
        assertEquals("[name]", client.execute("MAP_KEYS person").asProtocolLine());
    }

    @Test
    void protocolReturnsNotFoundEmptyAndErrorResponses() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        RedisClone.Response missingValue = client.execute("GET missing");
        RedisClone.Response emptyList = client.execute("MAP_KEYS missing");
        RedisClone.Response error = client.execute("SOMETHING");

        assertTrue(missingValue.isSuccess());
        assertEquals("NOT_FOUND", missingValue.asProtocolLine());
        assertTrue(emptyList.isSuccess());
        assertEquals("EMPTY", emptyList.asProtocolLine());
        assertFalse(error.isSuccess());
        assertEquals("ERROR Unknown command", error.asProtocolLine());
    }

    @Test
    void rejectsInvalidDirectApiArguments() {
        RedisClone.Client client = RedisClone.client(RedisClone.server());

        assertThrows(IllegalArgumentException.class, () -> client.set(" ", "value"));
        assertThrows(IllegalArgumentException.class, () -> client.append("key", null));
        assertThrows(IllegalArgumentException.class, () -> client.mapSet("map", "", "value"));
    }
}
