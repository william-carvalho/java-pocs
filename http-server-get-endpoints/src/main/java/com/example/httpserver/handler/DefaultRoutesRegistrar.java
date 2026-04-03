package com.example.httpserver.handler;

import com.example.httpserver.model.HttpResponse;
import com.example.httpserver.router.HttpRouter;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

public class DefaultRoutesRegistrar {

    public void register(HttpRouter router) {
        router.get("/", request -> HttpResponse.okText("HTTP Server is running"));

        router.get("/health", request -> HttpResponse.okJson("{\"status\":\"UP\"}"));

        router.get("/hello", request -> {
            String name = request.getQueryParam("name");
            String value = name == null || name.trim().isEmpty() ? "World" : name;
            return HttpResponse.okText("Hello, " + value);
        });

        router.get("/time", request ->
                HttpResponse.okJson("{\"serverTime\":\"" + LocalDateTime.now() + "\"}"));

        router.get("/echo", request -> HttpResponse.okJson(toJson(request.getQueryParams())));
    }

    private String toJson(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        Iterator<Map.Entry<String, String>> iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append("\"").append(escape(entry.getKey())).append("\":")
                    .append("\"").append(escape(entry.getValue())).append("\"");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

