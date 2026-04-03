package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.ShowRequest;
import com.example.ticketsystem.dto.ShowResponse;
import com.example.ticketsystem.entity.ShowEvent;
import com.example.ticketsystem.exception.BusinessValidationException;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.repository.ShowEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {

    private final ShowEventRepository repository;

    public ShowService(ShowEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ShowResponse create(ShowRequest request) {
        String name = request.getName().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new BusinessValidationException("Show with this name already exists");
        }

        ShowEvent entity = new ShowEvent();
        entity.setName(name);
        entity.setDescription(normalize(request.getDescription()));
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> list() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(ShowEvent::getId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowEvent findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found for id " + id));
    }

    private ShowResponse toResponse(ShowEvent entity) {
        ShowResponse response = new ShowResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
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
