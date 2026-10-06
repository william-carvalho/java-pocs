package com.example.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        out.value(date.toString());
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String value = in.nextString();
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IOException("Data invalida: " + value, exception);
        }
    }
}
