package com.example.gson.adapter;

import com.example.gson.model.Money;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public final class MoneyTypeAdapter extends TypeAdapter<Money> {
    @Override
    public void write(JsonWriter out, Money money) throws IOException {
        if (money == null) {
            out.nullValue();
            return;
        }
        out.value(money.getCurrency() + " " + money.getAmount().toPlainString());
    }

    @Override
    public Money read(JsonReader in) throws IOException {
        String value = in.nextString();
        String[] parts = value.split(" ", 2);
        if (parts.length != 2) {
            throw new IOException("Valor monetario invalido: " + value);
        }
        try {
            return new Money(parts[0], new BigDecimal(parts[1]));
        } catch (NumberFormatException exception) {
            throw new IOException("Valor monetario invalido: " + value, exception);
        }
    }
}
