package com.example.httpserver.server;

import com.example.httpserver.exception.BadHttpRequestException;
import com.example.httpserver.http.HttpRequestParser;
import com.example.httpserver.http.HttpResponseWriter;
import com.example.httpserver.model.HttpRequest;
import com.example.httpserver.model.HttpResponse;
import com.example.httpserver.router.HttpRouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnectionHandler implements Runnable {

    private final Socket socket;
    private final HttpRouter router;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;

    public ClientConnectionHandler(Socket socket,
                                   HttpRouter router,
                                   HttpRequestParser requestParser,
                                   HttpResponseWriter responseWriter) {
        this.socket = socket;
        this.router = router;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
    }

    @Override
    public void run() {
        try (Socket clientSocket = socket;
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {

            HttpRequest request = requestParser.parse(reader);
            HttpResponse response = router.route(request);
            responseWriter.write(clientSocket.getOutputStream(), response);
        } catch (BadHttpRequestException exception) {
            writeFallbackResponse(HttpResponse.badRequest(exception.getMessage()));
        } catch (Exception exception) {
            writeFallbackResponse(HttpResponse.internalServerError("Internal Server Error"));
        }
    }

    private void writeFallbackResponse(HttpResponse response) {
        try {
            responseWriter.write(socket.getOutputStream(), response);
        } catch (IOException ignored) {
        }
    }
}

