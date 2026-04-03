package com.example.httpserver.demo;

import com.example.httpserver.handler.DefaultRoutesRegistrar;
import com.example.httpserver.router.HttpRouter;
import com.example.httpserver.server.SimpleHttpServer;

public class HttpServerApplication {

    public static void main(String[] args) throws Exception {
        int port = args != null && args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        HttpRouter router = new HttpRouter();
        new DefaultRoutesRegistrar().register(router);

        System.out.println("Server started on port " + port);
        System.out.println("Available endpoints:");
        System.out.println("GET /");
        System.out.println("GET /health");
        System.out.println("GET /hello");
        System.out.println("GET /time");
        System.out.println("GET /echo");

        new SimpleHttpServer(port, router).start();
    }
}

