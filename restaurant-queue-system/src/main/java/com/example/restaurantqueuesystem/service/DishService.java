package com.example.restaurantqueuesystem.service;

import com.example.restaurantqueuesystem.dto.DishRequest;
import com.example.restaurantqueuesystem.dto.DishResponse;
import com.example.restaurantqueuesystem.entity.Dish;
import com.example.restaurantqueuesystem.exception.BusinessValidationException;
import com.example.restaurantqueuesystem.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Transactional
    public DishResponse createDish(DishRequest request) {
        if (dishRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BusinessValidationException("Dish with this name already exists");
        }

        Dish dish = new Dish();
        dish.setName(request.getName().trim());
        dish.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        return toResponse(dishRepository.save(dish));
    }

    @Transactional(readOnly = true)
    public List<DishResponse> listDishes() {
        return dishRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dish findDishOrThrow(Long dishId) {
        return dishRepository.findById(dishId)
                .orElseThrow(() -> new com.example.restaurantqueuesystem.exception.ResourceNotFoundException("Dish not found for id " + dishId));
    }

    private DishResponse toResponse(Dish dish) {
        DishResponse response = new DishResponse();
        response.setId(dish.getId());
        response.setName(dish.getName());
        response.setPreparationTimeMinutes(dish.getPreparationTimeMinutes());
        return response;
    }
}
