package com.example.httpserver.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryParamParser {

    private QueryParamParser() {
    }

    public static Map<String, String> parse(String queryString) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (queryString == null || queryString.trim().isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }

            String[] keyValue = pair.split("=", 2);
            String key = decode(keyValue[0]);
            String value = keyValue.length > 1 ? decode(keyValue[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException("UTF-8 should always be available", exception);
        }
    }
}

