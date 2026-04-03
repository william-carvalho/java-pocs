package com.example.httpserver.http;

import com.example.httpserver.exception.BadHttpRequestException;
import com.example.httpserver.model.HttpRequest;
import com.example.httpserver.util.QueryParamParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new BadHttpRequestException("Empty request line");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new BadHttpRequestException("Invalid HTTP request line");
        }

        String method = parts[0];
        String rawPath = parts[1];
        String httpVersion = parts[2];

        Map<String, String> headers = readHeaders(reader);

        String path = rawPath;
        String queryString = "";
        int separatorIndex = rawPath.indexOf('?');
        if (separatorIndex >= 0) {
            path = rawPath.substring(0, separatorIndex);
            queryString = rawPath.substring(separatorIndex + 1);
        }

        return new HttpRequest(
                method,
                rawPath,
                path,
                httpVersion,
                headers,
                QueryParamParser.parse(queryString)
        );
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<String, String>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                break;
            }

            int separatorIndex = line.indexOf(':');
            if (separatorIndex <= 0) {
                throw new BadHttpRequestException("Invalid HTTP header: " + line);
            }

            String name = line.substring(0, separatorIndex).trim();
            String value = line.substring(separatorIndex + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }
}

