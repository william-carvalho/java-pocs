package com.example.restaurantqueuesystem.config;

import com.example.restaurantqueuesystem.entity.Dish;
import com.example.restaurantqueuesystem.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class InitialDataLoader {

    @Bean
    public CommandLineRunner loadInitialData(DishRepository dishRepository) {
        return args -> {
            if (dishRepository.count() > 0) {
                return;
            }

            dishRepository.saveAll(Arrays.asList(
                    buildDish("Burger", 15),
                    buildDish("Pizza", 20),
                    buildDish("Salad", 8),
                    buildDish("Pasta", 18)
            ));
        };
    }

    private Dish buildDish(String name, Integer preparationTimeMinutes) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setPreparationTimeMinutes(preparationTimeMinutes);
        return dish;
    }
}
