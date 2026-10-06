package com.example.gson;

import com.example.gson.adapter.MoneyTypeAdapter;
import com.example.gson.adapter.LocalDateTypeAdapter;
import com.example.gson.model.Money;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

import java.time.LocalDate;

public final class GsonFactory {
    private GsonFactory() {
    }

    public static Gson create() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setStrictness(Strictness.STRICT)
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter().nullSafe())
                .registerTypeAdapter(Money.class, new MoneyTypeAdapter().nullSafe())
                .create();
    }
}
