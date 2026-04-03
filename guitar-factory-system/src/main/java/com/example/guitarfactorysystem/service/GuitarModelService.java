package com.example.guitarfactorysystem.service;

import com.example.guitarfactorysystem.dto.GuitarModelRequest;
import com.example.guitarfactorysystem.dto.GuitarModelResponse;
import com.example.guitarfactorysystem.dto.GuitarModelSummaryResponse;
import com.example.guitarfactorysystem.entity.GuitarModel;
import com.example.guitarfactorysystem.exception.BusinessValidationException;
import com.example.guitarfactorysystem.exception.ResourceNotFoundException;
import com.example.guitarfactorysystem.repository.GuitarModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuitarModelService {

    private final GuitarModelRepository repository;

    public GuitarModelService(GuitarModelRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GuitarModelResponse create(GuitarModelRequest request) {
        String name = request.getName().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new BusinessValidationException("Guitar model with this name already exists");
        }

        GuitarModel entity = new GuitarModel();
        entity.setName(name);
        entity.setDescription(normalize(request.getDescription()));
        entity.setBasePrice(request.getBasePrice());
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<GuitarModelResponse> list() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuitarModel findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guitar model not found for id " + id));
    }

    public GuitarModelSummaryResponse toSummary(GuitarModel model) {
        GuitarModelSummaryResponse response = new GuitarModelSummaryResponse();
        response.setId(model.getId());
        response.setName(model.getName());
        return response;
    }

    private GuitarModelResponse toResponse(GuitarModel entity) {
        GuitarModelResponse response = new GuitarModelResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setBasePrice(entity.getBasePrice());
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
