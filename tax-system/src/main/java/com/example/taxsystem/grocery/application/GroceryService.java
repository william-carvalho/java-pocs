package com.example.taxsystem.grocery.application;

import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.grocery.api.CreateGroceryItemRequest;
import com.example.taxsystem.grocery.api.GroceryItemResponse;
import com.example.taxsystem.grocery.domain.GroceryItem;
import com.example.taxsystem.grocery.domain.GroceryStatus;
import com.example.taxsystem.grocery.infrastructure.GroceryItemEntity;
import com.example.taxsystem.grocery.infrastructure.GroceryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
public class GroceryService {

    private final GroceryRepository groceryRepository;
    private final ConverterEngine converterEngine;

    public GroceryService(GroceryRepository groceryRepository, ConverterEngine converterEngine) {
        this.groceryRepository = groceryRepository;
        this.converterEngine = converterEngine;
    }

    @Transactional
    @CacheEvict(value = "grocery-items", allEntries = true)
    public GroceryItemResponse create(CreateGroceryItemRequest request) {
        GroceryItemEntity entity = new GroceryItemEntity();
        entity.setName(request.getName().trim());
        entity.setStatus(GroceryStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(entity.getCreatedAt());
        return toResponse(groceryRepository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "grocery-items", allEntries = true)
    public void remove(Long itemId) {
        GroceryItemEntity entity = getRequiredEntity(itemId);
        groceryRepository.delete(entity);
    }

    @Transactional
    @CacheEvict(value = "grocery-items", allEntries = true)
    public GroceryItemResponse markDone(Long itemId) {
        return update(itemId, entity -> {
            entity.setStatus(GroceryStatus.DONE);
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setCompletedAt(LocalDateTime.now());
            return entity;
        });
    }

    @Transactional
    @CacheEvict(value = "grocery-items", allEntries = true)
    public GroceryItemResponse redo(Long itemId) {
        return update(itemId, entity -> {
            entity.setStatus(GroceryStatus.PENDING);
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setCompletedAt(null);
            return entity;
        });
    }

    @Transactional
    @CacheEvict(value = "grocery-items", allEntries = true)
    public GroceryItemResponse doItem(Long itemId) {
        return markDone(itemId);
    }

    @Transactional(readOnly = true)
    @Cacheable("grocery-items")
    public List<GroceryItemResponse> listAll() {
        return mapResponses(groceryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public List<GroceryItemResponse> listByStatus(GroceryStatus status) {
        return mapResponses(groceryRepository.findAllByStatusOrderByCreatedAtDesc(status));
    }

    @Transactional(readOnly = true)
    public List<GroceryItemResponse> searchByName(String name) {
        return mapResponses(groceryRepository.findAllByNameContainingIgnoreCaseOrderByCreatedAtDesc(name));
    }

    @Transactional(readOnly = true)
    public List<GroceryItemResponse> list(String name, GroceryStatus status) {
        boolean hasNameFilter = name != null && !name.trim().isEmpty();
        if (hasNameFilter && status != null) {
            String normalizedName = name.trim().toLowerCase(Locale.ENGLISH);
            List<GroceryItemEntity> filtered = groceryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                    .stream()
                    .filter(entity -> entity.getStatus() == status)
                    .filter(entity -> entity.getName().toLowerCase(Locale.ENGLISH).contains(normalizedName))
                    .collect(Collectors.toList());
            return mapResponses(filtered);
        }
        if (hasNameFilter) {
            return searchByName(name);
        }
        if (status != null) {
            return listByStatus(status);
        }
        return listAll();
    }

    private GroceryItemResponse update(Long itemId, UnaryOperator<GroceryItemEntity> updater) {
        GroceryItemEntity entity = getRequiredEntity(itemId);
        return toResponse(groceryRepository.save(updater.apply(entity)));
    }

    private GroceryItemEntity getRequiredEntity(Long itemId) {
        return groceryRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Grocery item not found: " + itemId));
    }

    private List<GroceryItemResponse> mapResponses(List<GroceryItemEntity> entities) {
        List<GroceryItem> items = entities.stream()
                .map(entity -> converterEngine.convert(entity, GroceryItem.class))
                .collect(Collectors.toList());
        return converterEngine.convertCollection(items, GroceryItemResponse.class);
    }

    private GroceryItemResponse toResponse(GroceryItemEntity entity) {
        GroceryItem item = converterEngine.convert(entity, GroceryItem.class);
        return converterEngine.convert(item, GroceryItemResponse.class);
    }
}
