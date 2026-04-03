package com.example.redisclone.protocol;

public final class ProtocolConstants {

    public static final int DEFAULT_PORT = 6379;
    public static final String DEFAULT_HOST = "localhost";

    public static final String OK = "OK";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String REMOVED = "REMOVED";
    public static final String EMPTY = "EMPTY";
    public static final String BYE = "BYE";
    public static final String ERROR_PREFIX = "ERROR ";
    public static final String VALUE_PREFIX = "VALUE ";
    public static final String KEYS_PREFIX = "KEYS ";
    public static final String VALUES_PREFIX = "VALUES ";

    private ProtocolConstants() {
    }
}

