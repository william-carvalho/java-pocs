package com.example.redisclone.command;

import com.example.redisclone.protocol.ProtocolConstants;
import com.example.redisclone.storage.InMemoryDataStore;

import java.util.List;

public class CommandProcessor {

    private final InMemoryDataStore dataStore;

    public CommandProcessor(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String process(ParsedCommand parsedCommand) {
        List<String> args = parsedCommand.getArguments();

        switch (parsedCommand.getCommandType()) {
            case SET_STRING:
                dataStore.setString(args.get(0), args.get(1));
                return ProtocolConstants.OK;
            case GET_STRING:
                return toValueResponse(dataStore.getString(args.get(0)));
            case REMOVE_STRING:
                return dataStore.removeString(args.get(0)) ? ProtocolConstants.REMOVED : ProtocolConstants.NOT_FOUND;
            case APPEND_STRING:
                return ProtocolConstants.VALUE_PREFIX + dataStore.appendString(args.get(0), args.get(1));
            case MAP_SET:
                dataStore.mapSet(args.get(0), args.get(1), args.get(2));
                return ProtocolConstants.OK;
            case MAP_GET:
                return toValueResponse(dataStore.mapGet(args.get(0), args.get(1)));
            case MAP_KEYS:
                return dataStore.mapKeys(args.get(0)).isEmpty()
                        ? ProtocolConstants.EMPTY
                        : ProtocolConstants.KEYS_PREFIX + dataStore.mapKeys(args.get(0));
            case MAP_VALUES:
                return dataStore.mapValues(args.get(0)).isEmpty()
                        ? ProtocolConstants.EMPTY
                        : ProtocolConstants.VALUES_PREFIX + dataStore.mapValues(args.get(0));
            case QUIT:
                return ProtocolConstants.BYE;
            default:
                return ProtocolConstants.ERROR_PREFIX + "Unknown command";
        }
    }

    private String toValueResponse(String value) {
        return value == null ? ProtocolConstants.NOT_FOUND : ProtocolConstants.VALUE_PREFIX + value;
    }
}

