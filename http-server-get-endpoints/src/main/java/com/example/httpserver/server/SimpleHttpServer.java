package com.example.httpserver.server;

import com.example.httpserver.http.HttpRequestParser;
import com.example.httpserver.http.HttpResponseWriter;
import com.example.httpserver.router.HttpRouter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private final int port;
    private final HttpRouter router;
    private final ExecutorService executorService;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;

    public SimpleHttpServer(int port, HttpRouter router) {
        this.port = port;
        this.router = router;
        this.executorService = Executors.newCachedThreadPool();
        this.requestParser = new HttpRequestParser();
        this.responseWriter = new HttpResponseWriter();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(new ClientConnectionHandler(clientSocket, router, requestParser, responseWriter));
        }
    }
}

