package com.example.redisclone.handler;

import com.example.redisclone.command.CommandParser;
import com.example.redisclone.command.CommandProcessor;
import com.example.redisclone.command.ParsedCommand;
import com.example.redisclone.exception.CommandParseException;
import com.example.redisclone.protocol.ProtocolConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CommandParser commandParser;
    private final CommandProcessor commandProcessor;

    public ClientHandler(Socket socket, CommandParser commandParser, CommandProcessor commandProcessor) {
        this.socket = socket;
        this.commandParser = commandParser;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        try (Socket clientSocket = socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String response = handleLine(line);
                writer.println(response);

                if (ProtocolConstants.BYE.equals(response)) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.err.println("Client connection closed with error: " + ex.getMessage());
        }
    }

    private String handleLine(String line) {
        try {
            ParsedCommand parsedCommand = commandParser.parse(line);
            return commandProcessor.process(parsedCommand);
        } catch (CommandParseException ex) {
            return ProtocolConstants.ERROR_PREFIX + ex.getMessage();
        } catch (RuntimeException ex) {
            return ProtocolConstants.ERROR_PREFIX + "Internal server error";
        }
    }
}

