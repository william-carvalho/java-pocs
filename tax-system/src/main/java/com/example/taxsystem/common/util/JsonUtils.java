package com.example.taxsystem.common.util;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {

    private final Gson gson;

    public JsonUtils(Gson gson) {
        this.gson = gson;
    }

    public String toJson(Object value) {
        return gson.toJson(value);
    }
}
