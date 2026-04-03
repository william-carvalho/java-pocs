package com.example.httpserver.http;

import com.example.httpserver.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponseWriter {

    public void write(OutputStream outputStream, HttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ")
                .append(response.getStatusCode())
                .append(" ")
                .append(response.getReasonPhrase())
                .append("\r\n");

        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            builder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        builder.append("\r\n");

        outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(response.getBody());
        outputStream.flush();
    }
}

