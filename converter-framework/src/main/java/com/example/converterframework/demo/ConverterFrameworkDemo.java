package com.example.converterframework.demo;

import com.example.converterframework.exception.ConverterNotFoundException;
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

public class ConverterFrameworkDemo {

    public static void main(String[] args) {
        ConversionService conversionService = FrameworkFactory.createDefaultConversionService();

        AddressEntity address = new AddressEntity("Street 1", "Floripa", "88000-000");
        UserEntity user = new UserEntity(1L, "William Carvalho", "william@email.com", address);

        ProductEntity notebook = new ProductEntity(10L, "Notebook", new BigDecimal("4500.00"), "Electronics");
        ProductEntity mouse = new ProductEntity(11L, "Mouse", new BigDecimal("120.00"), "Electronics");

        OrderEntity order = new OrderEntity(
                100L,
                user,
                Arrays.asList(
                        new OrderItemEntity(notebook, 1),
                        new OrderItemEntity(mouse, 2)
                ),
                new BigDecimal("4740.00")
        );

        UserDTO userDTO = conversionService.convert(user, UserDTO.class);
        ProductDTO productDTO = conversionService.convert(notebook, ProductDTO.class);
        OrderResponse orderResponse = conversionService.convert(order, OrderResponse.class);
        List<ProductDTO> productDTOList = conversionService.convertList(Arrays.asList(notebook, mouse), ProductDTO.class);

        System.out.println("UserEntity -> UserDTO");
        System.out.println(userDTO);
        System.out.println();

        System.out.println("ProductEntity -> ProductDTO");
        System.out.println(productDTO);
        System.out.println();

        System.out.println("OrderEntity -> OrderResponse");
        System.out.println(orderResponse);
        System.out.println();

        System.out.println("List<ProductEntity> -> List<ProductDTO>");
        System.out.println(productDTOList);
        System.out.println();

        try {
            conversionService.convert(user, UnknownDTO.class);
        } catch (ConverterNotFoundException ex) {
            System.out.println("Expected error:");
            System.out.println(ex.getMessage());
        }
    }
}

