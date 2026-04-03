package com.example.redisclone;

import com.example.redisclone.command.CommandParser;
import com.example.redisclone.command.CommandProcessor;
import com.example.redisclone.exception.CommandParseException;
import com.example.redisclone.protocol.ProtocolConstants;
import com.example.redisclone.storage.InMemoryDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandProcessorTest {

    private CommandParser commandParser;
    private CommandProcessor commandProcessor;

    @BeforeEach
    void setUp() {
        commandParser = new CommandParser();
        commandProcessor = new CommandProcessor(new InMemoryDataStore());
    }

    @Test
    void shouldHandleStringCommands() {
        assertEquals(ProtocolConstants.OK, process("SET_STRING name William"));
        assertEquals("VALUE William", process("GET_STRING name"));
        assertEquals("VALUE WilliamSilva", process("APPEND_STRING name Silva"));
        assertEquals(ProtocolConstants.REMOVED, process("REMOVE_STRING name"));
        assertEquals(ProtocolConstants.NOT_FOUND, process("GET_STRING name"));
    }

    @Test
    void shouldCreateStringOnAppendWhenKeyDoesNotExist() {
        assertEquals("VALUE Floripa", process("APPEND_STRING city Floripa"));
        assertEquals("VALUE Floripa", process("GET_STRING city"));
    }

    @Test
    void shouldHandleMapCommands() {
        assertEquals(ProtocolConstants.OK, process("MAP_SET user name William"));
        assertEquals(ProtocolConstants.OK, process("MAP_SET user role Admin"));
        assertEquals("VALUE William", process("MAP_GET user name"));
        assertEquals("KEYS [name, role]", process("MAP_KEYS user"));
        assertEquals("VALUES [William, Admin]", process("MAP_VALUES user"));
        assertEquals(ProtocolConstants.EMPTY, process("MAP_KEYS missing"));
    }

    @Test
    void shouldRejectInvalidCommands() {
        assertThrows(CommandParseException.class, () -> commandParser.parse("UNKNOWN key value"));
        assertThrows(CommandParseException.class, () -> commandParser.parse("SET_STRING onlyKey"));
    }

    private String process(String line) {
        return commandProcessor.process(commandParser.parse(line));
    }
}

