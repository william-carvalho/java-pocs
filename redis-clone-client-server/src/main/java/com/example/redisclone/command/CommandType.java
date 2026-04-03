package com.example.redisclone.command;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    SET_STRING(3),
    GET_STRING(2),
    REMOVE_STRING(2),
    APPEND_STRING(3),
    MAP_SET(4),
    MAP_GET(3),
    MAP_KEYS(2),
    MAP_VALUES(2),
    QUIT(1);

    private static final Map<String, CommandType> LOOKUP = new HashMap<String, CommandType>();

    static {
        for (CommandType commandType : values()) {
            LOOKUP.put(commandType.name(), commandType);
        }
    }

    private final int argumentCount;

    CommandType(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public static CommandType fromToken(String token) {
        if (token == null) {
            return null;
        }
        return LOOKUP.get(token.toUpperCase());
    }
}

