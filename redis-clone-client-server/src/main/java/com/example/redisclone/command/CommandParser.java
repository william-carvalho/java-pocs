package com.example.redisclone.command;

import com.example.redisclone.exception.CommandParseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandParser {

    public ParsedCommand parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new CommandParseException("Invalid arguments");
        }

        String[] tokens = line.trim().split("\\s+");
        CommandType commandType = CommandType.fromToken(tokens[0]);
        if (commandType == null) {
            throw new CommandParseException("Unknown command");
        }

        if (tokens.length != commandType.getArgumentCount()) {
            throw new CommandParseException("Invalid arguments");
        }

        List<String> arguments = tokens.length > 1
                ? Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length))
                : Collections.<String>emptyList();

        return new ParsedCommand(commandType, arguments);
    }
}

