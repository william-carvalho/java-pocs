package com.example.converterframework.converter;

import com.example.converterframework.core.Converter;
import com.example.converterframework.model.dto.AddressDTO;
import com.example.converterframework.model.dto.UserDTO;
import com.example.converterframework.model.entity.UserEntity;
import com.example.converterframework.service.ConversionService;

public class UserEntityToUserDTOConverter implements Converter<UserEntity, UserDTO> {

    @Override
    public UserDTO convert(UserEntity source, ConversionService conversionService) {
        UserDTO target = new UserDTO();
        target.setId(source.getId());
        target.setFullName(source.getName());
        target.setEmail(source.getEmail());
        target.setAddress(conversionService.convert(source.getAddress(), AddressDTO.class));
        return target;
    }
}

