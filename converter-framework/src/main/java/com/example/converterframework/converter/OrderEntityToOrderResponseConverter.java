package com.example.converterframework.converter;

import com.example.converterframework.core.Converter;
import com.example.converterframework.model.dto.OrderResponse;
import com.example.converterframework.model.entity.OrderEntity;
import com.example.converterframework.service.ConversionService;

public class OrderEntityToOrderResponseConverter implements Converter<OrderEntity, OrderResponse> {

    @Override
    public OrderResponse convert(OrderEntity source, ConversionService conversionService) {
        OrderResponse target = new OrderResponse();
        target.setId(source.getId());
        target.setCustomerName(source.getCustomer().getName());
        target.setItemCount(source.getItems() == null ? 0 : source.getItems().size());
        target.setTotalAmount(source.getTotalAmount());
        return target;
    }
}
