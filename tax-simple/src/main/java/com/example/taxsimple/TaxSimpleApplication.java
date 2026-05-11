package com.example.taxsimple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootApplication
@RestController
public class TaxSimpleApplication {

    private static final Map<String, BigDecimal> TAXES = new HashMap<String, BigDecimal>();

    static {
        TAXES.put("FOOD-SP-2024", new BigDecimal("0.07"));
        TAXES.put("FOOD-RJ-2024", new BigDecimal("0.08"));
        TAXES.put("BOOK-SP-2024", new BigDecimal("0.04"));
        TAXES.put("ELECTRONIC-SP-2025", new BigDecimal("0.18"));
        TAXES.put("ELECTRONIC-MG-2025", new BigDecimal("0.16"));
    }

    public static void main(String[] args) {
        SpringApplication.run(TaxSimpleApplication.class, args);
    }

    @GetMapping("/tax")
    public Map<String, Object> calculate(@RequestParam String product,
                                         @RequestParam String state,
                                         @RequestParam int year,
                                         @RequestParam BigDecimal price) {
        String key = product.toUpperCase() + "-" + state.toUpperCase() + "-" + year;
        BigDecimal taxPercent = TAXES.get(key);

        if (taxPercent == null) {
            throw new ResponseStatusException(NOT_FOUND, "Tax not found for " + key);
        }

        BigDecimal taxValue = price.multiply(taxPercent).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = price.add(taxValue).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("product", product.toUpperCase());
        response.put("state", state.toUpperCase());
        response.put("year", year);
        response.put("price", price);
        response.put("taxPercent", taxPercent);
        response.put("taxValue", taxValue);
        response.put("finalPrice", finalPrice);
        return response;
    }
}
