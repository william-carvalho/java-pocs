package com.example.httpserver.router;

import com.example.httpserver.handler.RouteHandler;
import com.example.httpserver.model.HttpRequest;
import com.example.httpserver.model.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRouter {

    private final Map<String, RouteHandler> getRoutes = new ConcurrentHashMap<String, RouteHandler>();

    public void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    public HttpResponse route(HttpRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return HttpResponse.methodNotAllowed("Method Not Allowed");
        }

        RouteHandler handler = getRoutes.get(request.getPath());
        if (handler == null) {
            return HttpResponse.notFound("Not Found");
        }
        return handler.handle(request);
    }
}

