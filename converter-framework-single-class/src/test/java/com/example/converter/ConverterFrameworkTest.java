package com.example.converter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConverterFrameworkTest {
    @Test
    void convertsNestedComplexObjectWithRenamedFields() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();
        ConverterFramework.UserEntity user = new ConverterFramework.UserEntity(
                1L,
                "William Carvalho",
                "william@email.com",
                new ConverterFramework.AddressEntity("Street 1", "Floripa", "88000-000"));

        ConverterFramework.UserDTO dto = service.convert(user, ConverterFramework.UserDTO.class);

        assertEquals(1L, dto.getId());
        assertEquals("William Carvalho", dto.getFullName());
        assertEquals("william@email.com", dto.getEmailAddress());
        assertEquals("Street 1", dto.getAddress().getStreet());
        assertEquals("Floripa", dto.getAddress().getCity());
        assertEquals("88000-000", dto.getAddress().getPostalCode());
    }

    @Test
    void convertsProductWithDerivedPromotionalPrice() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();

        ConverterFramework.ProductDTO dto = service.convert(
                new ConverterFramework.ProductEntity("SKU-1", "Keyboard", "100.00"),
                ConverterFramework.ProductDTO.class);

        assertEquals("SKU-1", dto.getCode());
        assertEquals("Keyboard", dto.getDisplayName());
        assertEquals(new BigDecimal("100.00"), dto.getPrice());
        assertEquals(new BigDecimal("90.0000"), dto.getPromotionalPrice());
    }

    @Test
    void convertsLists() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();
        List<ConverterFramework.ProductEntity> products = Arrays.asList(
                new ConverterFramework.ProductEntity("P1", "Mouse", "50.00"),
                new ConverterFramework.ProductEntity("P2", "Monitor", "900.00"));

        List<ConverterFramework.ProductDTO> converted = service.convertList(products, ConverterFramework.ProductDTO.class);

        assertEquals(2, converted.size());
        assertEquals("P1", converted.get(0).getCode());
        assertEquals("Monitor", converted.get(1).getDisplayName());
    }

    @Test
    void convertsOrderWithNestedCustomerListAndDerivedFields() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();
        ConverterFramework.UserEntity customer = new ConverterFramework.UserEntity(
                10L,
                "Ana Silva",
                "ana@email.com",
                new ConverterFramework.AddressEntity("Main", "Sao Paulo", "01000-000"));
        ConverterFramework.OrderEntity order = new ConverterFramework.OrderEntity(
                99L,
                customer,
                Arrays.asList(
                        new ConverterFramework.ProductEntity("A", "Book", "30.00"),
                        new ConverterFramework.ProductEntity("B", "Pen", "5.50")));

        ConverterFramework.OrderResponse response = service.convert(order, ConverterFramework.OrderResponse.class);

        assertEquals(99L, response.getOrderId());
        assertEquals("Ana Silva", response.getCustomerName());
        assertEquals(2, response.getItemCount());
        assertEquals(new BigDecimal("35.50"), response.getTotal());
        assertEquals(2, response.getProducts().size());
        assertEquals("Book", response.getProducts().get(0).getDisplayName());
    }

    @Test
    void returnsNullWhenSourceIsNull() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();

        assertNull(service.convert(null, ConverterFramework.UserDTO.class));
    }

    @Test
    void returnsEmptyListWhenSourceListIsNull() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();

        assertEquals(0, service.convertList(null, ConverterFramework.ProductDTO.class).size());
    }

    @Test
    void throwsClearErrorWhenConverterDoesNotExist() {
        ConverterFramework.ConversionService service = ConverterFramework.defaultService();

        ConverterFramework.ConverterNotFoundException error = assertThrows(
                ConverterFramework.ConverterNotFoundException.class,
                () -> service.convert(new ConverterFramework.UserEntity(1L, "A", "a@x.com", null), ConverterFramework.UnknownDTO.class));

        assertEquals("No converter registered from UserEntity to UnknownDTO", error.getMessage());
    }

    @Test
    void supportsManualConverterRegistration() {
        ConverterFramework.ConverterRegistry registry = new ConverterFramework.ConverterRegistry();
        ConverterFramework.ConversionService service = new ConverterFramework.DefaultConversionService(registry);
        registry.register(ConverterFramework.AddressEntity.class, String.class, new ConverterFramework.Converter<ConverterFramework.AddressEntity, String>() {
            public String convert(ConverterFramework.AddressEntity source) {
                return source.getStreet() + ", " + source.getCity();
            }
        });

        String value = service.convert(
                new ConverterFramework.AddressEntity("Avenida Brasil", "Rio", "20000-000"),
                String.class);

        assertEquals("Avenida Brasil, Rio", value);
    }
}
