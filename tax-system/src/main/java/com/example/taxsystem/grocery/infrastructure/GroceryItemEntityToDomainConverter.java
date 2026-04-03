package com.example.taxsystem.grocery.infrastructure;

import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.converter.domain.Converter;
import com.example.taxsystem.grocery.domain.GroceryAudit;
import com.example.taxsystem.grocery.domain.GroceryItem;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GroceryItemEntityToDomainConverter implements Converter<GroceryItemEntity, GroceryItem> {

    @Override
    public Class<GroceryItemEntity> sourceType() {
        return GroceryItemEntity.class;
    }

    @Override
    public Class<GroceryItem> targetType() {
        return GroceryItem.class;
    }

    @Override
    public GroceryItem convert(GroceryItemEntity source, ConverterEngine converterEngine) {
        Function<GroceryItemEntity, GroceryAudit> auditMapper = entity -> {
            GroceryAudit audit = new GroceryAudit();
            audit.setCreatedAt(entity.getCreatedAt());
            audit.setUpdatedAt(entity.getUpdatedAt());
            audit.setCompletedAt(entity.getCompletedAt());
            return audit;
        };

        GroceryItem item = new GroceryItem();
        item.setId(source.getId());
        item.setName(source.getName());
        item.setStatus(source.getStatus());
        item.setAudit(auditMapper.apply(source));
        return item;
    }
}
