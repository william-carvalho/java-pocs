package com.example.converterframework;

import com.example.converterframework.exception.ConverterNotFoundException;
import com.example.converterframework.model.dto.AddressDTO;
import com.example.converterframework.model.dto.OrderResponse;
import com.example.converterframework.model.dto.ProductDTO;
import com.example.converterframework.model.dto.UnknownDTO;
import com.example.converterframework.model.dto.UserDTO;
import com.example.converterframework.model.entity.AddressEntity;
import com.example.converterframework.model.entity.OrderEntity;
import com.example.converterframework.model.entity.OrderItemEntity;
import com.example.converterframework.model.entity.ProductEntity;
import com.example.converterframework.model.entity.UserEntity;
import com.example.converterframework.service.ConversionService;
import com.example.converterframework.util.FrameworkFactory;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultConversionServiceTest {

    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = FrameworkFactory.createDefaultConversionService();
    }

    @Test
    void shouldConvertNestedObject() {
        AddressEntity addressEntity = new AddressEntity("Street 1", "Floripa", "88000-000");
        UserEntity userEntity = new UserEntity(1L, "William Carvalho", "william@email.com", addressEntity);

        UserDTO userDTO = conversionService.convert(userEntity, UserDTO.class);

        assertEquals(Long.valueOf(1L), userDTO.getId());
        assertEquals("William Carvalho", userDTO.getFullName());
        assertEquals("william@email.com", userDTO.getEmail());
        assertNotNull(userDTO.getAddress());
        assertEquals("88000-000", userDTO.getAddress().getPostalCode());
    }

    @Test
    void shouldConvertList() {
        List<ProductEntity> products = Arrays.asList(
                new ProductEntity(1L, "Notebook", new BigDecimal("4500.00"), "Electronics"),
                new ProductEntity(2L, "Mouse", new BigDecimal("120.00"), "Electronics")
        );

        List<ProductDTO> productDTOS = conversionService.convertList(products, ProductDTO.class);

        assertEquals(2, productDTOS.size());
        assertEquals("Notebook", productDTOS.get(0).getName());
        assertEquals(new BigDecimal("120.00"), productDTOS.get(1).getPrice());
    }

    @Test
    void shouldConvertDerivedFields() {
        UserEntity customer = new UserEntity(
                1L,
                "William Carvalho",
                "william@email.com",
                new AddressEntity("Street 1", "Floripa", "88000-000")
        );
        OrderEntity orderEntity = new OrderEntity(
                100L,
                customer,
                Arrays.asList(
                        new OrderItemEntity(new ProductEntity(1L, "Notebook", new BigDecimal("4500.00"), "Electronics"), 1),
                        new OrderItemEntity(new ProductEntity(2L, "Mouse", new BigDecimal("120.00"), "Electronics"), 2)
                ),
                new BigDecimal("4740.00")
        );

        OrderResponse orderResponse = conversionService.convert(orderEntity, OrderResponse.class);

        assertEquals(Long.valueOf(100L), orderResponse.getId());
        assertEquals("William Carvalho", orderResponse.getCustomerName());
        assertEquals(Integer.valueOf(2), orderResponse.getItemCount());
        assertEquals(new BigDecimal("4740.00"), orderResponse.getTotalAmount());
    }

    @Test
    void shouldThrowWhenConverterDoesNotExist() {
        UserEntity userEntity = new UserEntity(
                1L,
                "William Carvalho",
                "william@email.com",
                new AddressEntity("Street 1", "Floripa", "88000-000")
        );

        ConverterNotFoundException exception = assertThrows(
                ConverterNotFoundException.class,
                () -> conversionService.convert(userEntity, UnknownDTO.class)
        );

        assertEquals("No converter registered for source UserEntity and target UnknownDTO", exception.getMessage());
    }
}

