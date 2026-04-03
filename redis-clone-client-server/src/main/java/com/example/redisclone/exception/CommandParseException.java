package com.example.redisclone.exception;

public class CommandParseException extends RuntimeException {

    public CommandParseException(String message) {
        super(message);
    }
}

