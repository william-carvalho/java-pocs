package com.example.redisclone.server;

import com.example.redisclone.command.CommandParser;
import com.example.redisclone.command.CommandProcessor;
import com.example.redisclone.protocol.ProtocolConstants;
import com.example.redisclone.storage.InMemoryDataStore;
import com.example.redisclone.util.PortUtils;

public class RedisCloneServerApplication {

    public static void main(String[] args) throws Exception {
        int port = PortUtils.parsePort(args, 0, ProtocolConstants.DEFAULT_PORT);

        InMemoryDataStore dataStore = new InMemoryDataStore();
        CommandParser commandParser = new CommandParser();
        CommandProcessor commandProcessor = new CommandProcessor(dataStore);

        SocketServer socketServer = new SocketServer(port, commandParser, commandProcessor);
        socketServer.start();
    }
}

