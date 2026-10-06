package com.example.gson;

import com.example.gson.model.ApiResponse;
import com.example.gson.model.Customer;
import com.example.gson.model.Money;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GsonPocTest {
    private final Gson gson = GsonFactory.create();

    @Test
    void shouldRoundTripCustomerUsingAnnotationsJavaTimeAndCustomAdapter() {
        Customer original = customer();
        original.setInternalNote("nao serializar");

        String json = gson.toJson(original);
        Customer restored = gson.fromJson(json, Customer.class);

        assertTrue(json.contains("\"customer_id\": 7"));
        assertTrue(json.contains("\"creditLimit\": \"BRL 99.90\""));
        assertTrue(json.contains("\"nickname\": null"));
        assertFalse(json.contains("internalNote"));
        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getBirthDate(), restored.getBirthDate());
        assertEquals(original.getCreditLimit(), restored.getCreditLimit());
        assertNull(restored.getInternalNote());
    }

    @Test
    void shouldDeserializeGenericListWithoutLosingItsElementType() {
        Type type = new TypeToken<List<Customer>>() { }.getType();

        List<Customer> customers = gson.fromJson(gson.toJson(Arrays.asList(customer()), type), type);

        assertEquals(1, customers.size());
        assertEquals(Customer.class, customers.get(0).getClass());
        assertEquals("Maria", customers.get(0).getName());
    }

    @Test
    void shouldDeserializeParameterizedResponse() {
        Type type = TypeToken.getParameterized(ApiResponse.class, Customer.class).getType();

        ApiResponse<Customer> response = gson.fromJson(
                gson.toJson(new ApiResponse<Customer>(true, customer()), type), type);

        assertTrue(response.isSuccess());
        assertEquals("Maria", response.getData().getName());
    }

    @Test
    void shouldRejectMalformedJsonInStrictMode() {
        assertThrows(JsonParseException.class,
                () -> gson.fromJson("{'name': 'aspas simples'}", Customer.class));
    }

    private Customer customer() {
        return new Customer(
                7L,
                "Maria",
                "maria@example.com",
                LocalDate.of(1990, 1, 20),
                Arrays.asList("java", "apis"),
                new Money("BRL", new BigDecimal("99.90")),
                null
        );
    }
}
