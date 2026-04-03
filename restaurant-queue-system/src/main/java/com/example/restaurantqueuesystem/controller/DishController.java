package com.example.restaurantqueuesystem.controller;

import com.example.restaurantqueuesystem.dto.DishRequest;
import com.example.restaurantqueuesystem.dto.DishResponse;
import com.example.restaurantqueuesystem.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DishResponse createDish(@Valid @RequestBody DishRequest request) {
        return dishService.createDish(request);
    }

    @GetMapping
    public List<DishResponse> listDishes() {
        return dishService.listDishes();
    }
}
