package com.example.converterframework.converter;

import com.example.converterframework.core.Converter;
import com.example.converterframework.model.dto.ProductDTO;
import com.example.converterframework.model.entity.ProductEntity;
import com.example.converterframework.service.ConversionService;

public class ProductEntityToProductDTOConverter implements Converter<ProductEntity, ProductDTO> {

    @Override
    public ProductDTO convert(ProductEntity source, ConversionService conversionService) {
        ProductDTO target = new ProductDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPrice(source.getPrice());
        return target;
    }
}

