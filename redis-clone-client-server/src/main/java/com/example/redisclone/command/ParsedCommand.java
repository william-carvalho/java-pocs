package com.example.redisclone.command;

import java.util.List;

public class ParsedCommand {

    private final CommandType commandType;
    private final List<String> arguments;

    public ParsedCommand(CommandType commandType, List<String> arguments) {
        this.commandType = commandType;
        this.arguments = arguments;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public List<String> getArguments() {
        return arguments;
    }
}

