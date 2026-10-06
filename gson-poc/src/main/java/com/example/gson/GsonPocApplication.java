package com.example.gson;

import com.example.gson.model.ApiResponse;
import com.example.gson.model.Customer;
import com.example.gson.model.Money;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public final class GsonPocApplication {
    private GsonPocApplication() {
    }

    public static void main(String[] args) {
        Gson gson = GsonFactory.create();
        Customer customer = sampleCustomer();

        String json = gson.toJson(customer);
        System.out.println("1) Objeto Java -> JSON:\n" + json);

        Customer restored = gson.fromJson(json, Customer.class);
        System.out.println("\n2) JSON -> objeto Java: " + restored.getName());

        Type listType = new TypeToken<List<Customer>>() { }.getType();
        String listJson = gson.toJson(Arrays.asList(customer), listType);
        List<Customer> customers = gson.fromJson(listJson, listType);
        System.out.println("\n3) Lista generica: " + customers.size() + " cliente(s)");

        Type responseType = TypeToken.getParameterized(ApiResponse.class, Customer.class).getType();
        String responseJson = gson.toJson(new ApiResponse<Customer>(true, customer), responseType);
        ApiResponse<Customer> response = gson.fromJson(responseJson, responseType);
        System.out.println("\n4) Wrapper generico: success=" + response.isSuccess());

        JsonObject tree = JsonParser.parseString(json).getAsJsonObject();
        tree.addProperty("source", "gson-poc");
        System.out.println("\n5) API de arvore: source=" + tree.get("source").getAsString());
    }

    private static Customer sampleCustomer() {
        Customer customer = new Customer(
                42L,
                "Ana Souza",
                "ana@example.com",
                LocalDate.of(1992, 7, 15),
                Arrays.asList("java", "json"),
                new Money("BRL", new BigDecimal("1500.00")),
                null
        );
        customer.setInternalNote("Este campo transient nao vai para o JSON");
        return customer;
    }
}
