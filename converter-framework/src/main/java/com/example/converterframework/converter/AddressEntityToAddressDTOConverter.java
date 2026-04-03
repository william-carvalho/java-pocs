package com.example.converterframework.converter;

import com.example.converterframework.core.Converter;
import com.example.converterframework.model.dto.AddressDTO;
import com.example.converterframework.model.entity.AddressEntity;
import com.example.converterframework.service.ConversionService;

public class AddressEntityToAddressDTOConverter implements Converter<AddressEntity, AddressDTO> {

    @Override
    public AddressDTO convert(AddressEntity source, ConversionService conversionService) {
        AddressDTO target = new AddressDTO();
        target.setStreet(source.getStreet());
        target.setCity(source.getCity());
        target.setPostalCode(source.getZipCode());
        return target;
    }
}

