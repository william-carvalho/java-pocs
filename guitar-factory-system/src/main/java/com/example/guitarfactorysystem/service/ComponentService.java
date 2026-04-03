package com.example.guitarfactorysystem.service;

import com.example.guitarfactorysystem.dto.ComponentRequest;
import com.example.guitarfactorysystem.dto.ComponentResponse;
import com.example.guitarfactorysystem.dto.InventoryResponse;
import com.example.guitarfactorysystem.entity.GuitarComponent;
import com.example.guitarfactorysystem.exception.ResourceNotFoundException;
import com.example.guitarfactorysystem.repository.GuitarComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentService {

    private final GuitarComponentRepository repository;

    public ComponentService(GuitarComponentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ComponentResponse create(ComponentRequest request) {
        GuitarComponent entity = new GuitarComponent();
        entity.setName(request.getName().trim());
        entity.setCategory(request.getCategory());
        entity.setSpecificationValue(request.getSpecificationValue().trim());
        entity.setQuantityInStock(request.getQuantityInStock());
        entity.setUnitPrice(request.getUnitPrice());
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ComponentResponse> list() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .map(this::toInventoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuitarComponent findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found for id " + id));
    }

    private ComponentResponse toResponse(GuitarComponent entity) {
        ComponentResponse response = new ComponentResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setSpecificationValue(entity.getSpecificationValue());
        response.setQuantityInStock(entity.getQuantityInStock());
        response.setUnitPrice(entity.getUnitPrice());
        return response;
    }

    private InventoryResponse toInventoryResponse(GuitarComponent entity) {
        InventoryResponse response = new InventoryResponse();
        response.setComponentId(entity.getId());
        response.setComponentName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setSpecificationValue(entity.getSpecificationValue());
        response.setQuantityInStock(entity.getQuantityInStock());
        return response;
    }
}
