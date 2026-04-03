package com.example.grocerytodolistsystem.service;

import com.example.grocerytodolistsystem.dto.GroceryItemRequest;
import com.example.grocerytodolistsystem.dto.GroceryItemResponse;
import com.example.grocerytodolistsystem.entity.GroceryItem;
import com.example.grocerytodolistsystem.enums.ItemStatus;
import com.example.grocerytodolistsystem.exception.ResourceNotFoundException;
import com.example.grocerytodolistsystem.repository.GroceryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroceryItemService {

    private final GroceryItemRepository repository;

    public GroceryItemService(GroceryItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GroceryItemResponse create(GroceryItemRequest request) {
        GroceryItem entity = new GroceryItem();
        entity.setName(request.getName().trim());
        entity.setQuantity(normalize(request.getQuantity()));
        entity.setStatus(ItemStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(entity.getCreatedAt());
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<GroceryItemResponse> listAll(ItemStatus status) {
        List<GroceryItem> items = status == null
                ? repository.findAll().stream().sorted(Comparator.comparing(GroceryItem::getCreatedAt).thenComparing(GroceryItem::getId)).collect(Collectors.toList())
                : repository.findByStatusOrderByCreatedAtAscIdAsc(status);

        return items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroceryItemResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public void delete(Long id) {
        GroceryItem item = findOrThrow(id);
        repository.delete(item);
    }

    @Transactional
    public GroceryItemResponse markDone(Long id) {
        return updateStatus(id, ItemStatus.DONE);
    }

    @Transactional
    public GroceryItemResponse markDo(Long id) {
        return updateStatus(id, ItemStatus.PENDING);
    }

    @Transactional
    public GroceryItemResponse markRedo(Long id) {
        return updateStatus(id, ItemStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public GroceryItem findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found for id " + id));
    }

    private GroceryItemResponse updateStatus(Long id, ItemStatus status) {
        GroceryItem item = findOrThrow(id);
        item.setStatus(status);
        item.setUpdatedAt(LocalDateTime.now());
        return toResponse(item);
    }

    private GroceryItemResponse toResponse(GroceryItem entity) {
        GroceryItemResponse response = new GroceryItemResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setQuantity(entity.getQuantity());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
