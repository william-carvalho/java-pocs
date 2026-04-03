package com.example.converterframework.util;

import com.example.converterframework.converter.AddressEntityToAddressDTOConverter;
import com.example.converterframework.converter.OrderEntityToOrderResponseConverter;
import com.example.converterframework.converter.ProductEntityToProductDTOConverter;
import com.example.converterframework.converter.UserEntityToUserDTOConverter;
import com.example.converterframework.registry.ConverterRegistry;
import com.example.converterframework.service.ConversionService;
import com.example.converterframework.service.DefaultConversionService;
import com.example.converterframework.model.dto.AddressDTO;
import com.example.converterframework.model.dto.OrderResponse;
import com.example.converterframework.model.dto.ProductDTO;
import com.example.converterframework.model.dto.UserDTO;
import com.example.converterframework.model.entity.AddressEntity;
import com.example.converterframework.model.entity.OrderEntity;
import com.example.converterframework.model.entity.ProductEntity;
import com.example.converterframework.model.entity.UserEntity;

public final class FrameworkFactory {

    private FrameworkFactory() {
    }

    public static ConversionService createDefaultConversionService() {
        ConverterRegistry registry = new ConverterRegistry();
        ConversionService conversionService = new DefaultConversionService(registry);

        registry.register(AddressEntity.class, AddressDTO.class, new AddressEntityToAddressDTOConverter());
        registry.register(UserEntity.class, UserDTO.class, new UserEntityToUserDTOConverter());
        registry.register(ProductEntity.class, ProductDTO.class, new ProductEntityToProductDTOConverter());
        registry.register(OrderEntity.class, OrderResponse.class, new OrderEntityToOrderResponseConverter());

        return conversionService;
    }
}
