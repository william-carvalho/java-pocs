package com.example.redisclone.server;

import com.example.redisclone.command.CommandParser;
import com.example.redisclone.command.CommandProcessor;
import com.example.redisclone.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

    private final int port;
    private final CommandParser commandParser;
    private final CommandProcessor commandProcessor;
    private final ExecutorService executorService;

    public SocketServer(int port, CommandParser commandParser, CommandProcessor commandProcessor) {
        this.port = port;
        this.commandParser = commandParser;
        this.commandProcessor = commandProcessor;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Redis clone server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket, commandParser, commandProcessor));
            }
        } finally {
            executorService.shutdownNow();
        }
    }
}

