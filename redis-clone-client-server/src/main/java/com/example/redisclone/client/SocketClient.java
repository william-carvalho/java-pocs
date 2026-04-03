package com.example.redisclone.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient {

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startInteractiveSession() throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter serverWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Connected to " + host + ":" + port);
            System.out.println("Type commands. Use QUIT to close the client.");

            while (true) {
                System.out.print("> ");
                System.out.flush();

                String line = consoleReader.readLine();
                if (line == null) {
                    break;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                serverWriter.println(line);
                String response = serverReader.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                System.out.println(response);

                if ("QUIT".equalsIgnoreCase(line.trim())) {
                    break;
                }
            }
        }
    }
}
