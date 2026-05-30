package com.example.redisclone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisClone {
    private RedisClone() {
    }

    public static Server server() {
        return new Server();
    }

    public static Client client(Server server) {
        return new Client(server);
    }

    public static final class Client {
        private final Server server;

        private Client(Server server) {
            if (server == null) {
                throw new IllegalArgumentException("server is required");
            }
            this.server = server;
        }

        public String set(String key, String value) {
            return server.setString(key, value);
        }

        public String get(String key) {
            return server.getString(key);
        }

        public String remove(String key) {
            return server.removeString(key);
        }

        public String append(String key, String suffix) {
            return server.appendString(key, suffix);
        }

        public String mapSet(String mapName, String key, String value) {
            return server.mapSet(mapName, key, value);
        }

        public String mapGet(String mapName, String key) {
            return server.mapGet(mapName, key);
        }

        public List<String> mapKeys(String mapName) {
            return server.mapKeys(mapName);
        }

        public List<String> mapValues(String mapName) {
            return server.mapValues(mapName);
        }

        public Response execute(String commandLine) {
            return server.execute(commandLine);
        }
    }

    public static final class Server {
        private final Map<String, String> strings = new ConcurrentHashMap<String, String>();
        private final Map<String, Map<String, String>> maps = new ConcurrentHashMap<String, Map<String, String>>();

        public String setString(String key, String value) {
            requireKey(key, "key");
            requireValue(value, "value");
            strings.put(key, value);
            return "OK";
        }

        public String getString(String key) {
            requireKey(key, "key");
            return strings.get(key);
        }

        public String removeString(String key) {
            requireKey(key, "key");
            return strings.remove(key);
        }

        public String appendString(String key, String suffix) {
            requireKey(key, "key");
            requireValue(suffix, "suffix");
            String existing = strings.get(key);
            String updated = existing == null ? suffix : existing + suffix;
            strings.put(key, updated);
            return updated;
        }

        public String mapSet(String mapName, String key, String value) {
            requireKey(mapName, "mapName");
            requireKey(key, "key");
            requireValue(value, "value");
            mapFor(mapName).put(key, value);
            return "OK";
        }

        public String mapGet(String mapName, String key) {
            requireKey(mapName, "mapName");
            requireKey(key, "key");
            Map<String, String> map = maps.get(mapName);
            return map == null ? null : map.get(key);
        }

        public List<String> mapKeys(String mapName) {
            requireKey(mapName, "mapName");
            Map<String, String> map = maps.get(mapName);
            if (map == null) {
                return Collections.emptyList();
            }
            synchronized (map) {
                return Collections.unmodifiableList(new ArrayList<String>(map.keySet()));
            }
        }

        public List<String> mapValues(String mapName) {
            requireKey(mapName, "mapName");
            Map<String, String> map = maps.get(mapName);
            if (map == null) {
                return Collections.emptyList();
            }
            synchronized (map) {
                return Collections.unmodifiableList(new ArrayList<String>(map.values()));
            }
        }

        public Response execute(String commandLine) {
            if (isBlank(commandLine)) {
                return Response.error("Command is required");
            }

            String[] tokens = commandLine.trim().split("\\s+");
            String command = tokens[0].toUpperCase();
            try {
                if ("SET".equals(command) || "SET_STRING".equals(command)) {
                    requireTokenCount(tokens, 3);
                    setString(tokens[1], tokens[2]);
                    return Response.ok("OK");
                }
                if ("GET".equals(command) || "GET_STRING".equals(command)) {
                    requireTokenCount(tokens, 2);
                    return Response.value(getString(tokens[1]));
                }
                if ("REMOVE".equals(command) || "REMOVE_STRING".equals(command)) {
                    requireTokenCount(tokens, 2);
                    return Response.value(removeString(tokens[1]));
                }
                if ("APPEND".equals(command) || "APPEND_STRING".equals(command)) {
                    requireTokenCount(tokens, 3);
                    return Response.value(appendString(tokens[1], tokens[2]));
                }
                if ("MAP_SET".equals(command)) {
                    requireTokenCount(tokens, 4);
                    mapSet(tokens[1], tokens[2], tokens[3]);
                    return Response.ok("OK");
                }
                if ("MAP_GET".equals(command)) {
                    requireTokenCount(tokens, 3);
                    return Response.value(mapGet(tokens[1], tokens[2]));
                }
                if ("MAP_KEYS".equals(command)) {
                    requireTokenCount(tokens, 2);
                    return Response.list(mapKeys(tokens[1]));
                }
                if ("MAP_VALUES".equals(command)) {
                    requireTokenCount(tokens, 2);
                    return Response.list(mapValues(tokens[1]));
                }
                return Response.error("Unknown command");
            } catch (IllegalArgumentException ex) {
                return Response.error(ex.getMessage());
            }
        }

        private Map<String, String> mapFor(String mapName) {
            Map<String, String> existing = maps.get(mapName);
            if (existing != null) {
                return existing;
            }
            Map<String, String> created = Collections.synchronizedMap(new LinkedHashMap<String, String>());
            Map<String, String> previous = maps.putIfAbsent(mapName, created);
            return previous == null ? created : previous;
        }

        private static void requireTokenCount(String[] tokens, int expected) {
            if (tokens.length != expected) {
                throw new IllegalArgumentException("Invalid arguments");
            }
        }
    }

    public static final class Response {
        private final boolean success;
        private final String message;
        private final String value;
        private final List<String> values;

        private Response(boolean success, String message, String value, List<String> values) {
            this.success = success;
            this.message = message;
            this.value = value;
            this.values = values == null ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<String>(values));
        }

        public static Response ok(String message) {
            return new Response(true, message, null, null);
        }

        public static Response value(String value) {
            return new Response(true, value == null ? "NOT_FOUND" : "VALUE", value, null);
        }

        public static Response list(List<String> values) {
            return new Response(true, values == null || values.isEmpty() ? "EMPTY" : "LIST", null, values);
        }

        public static Response error(String message) {
            return new Response(false, message, null, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getValue() {
            return value;
        }

        public List<String> getValues() {
            return values;
        }

        public String asProtocolLine() {
            if (!success) {
                return "ERROR " + message;
            }
            if ("OK".equals(message)) {
                return "OK";
            }
            if ("NOT_FOUND".equals(message)) {
                return "NOT_FOUND";
            }
            if ("EMPTY".equals(message)) {
                return "EMPTY";
            }
            if ("LIST".equals(message)) {
                return values.toString();
            }
            return "VALUE " + value;
        }
    }

    private static void requireKey(String value, String name) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireValue(String value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
