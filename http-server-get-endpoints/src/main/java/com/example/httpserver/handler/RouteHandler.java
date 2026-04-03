package com.example.httpserver.handler;

import com.example.httpserver.model.HttpRequest;
import com.example.httpserver.model.HttpResponse;

public interface RouteHandler {

    HttpResponse handle(HttpRequest request);
}

