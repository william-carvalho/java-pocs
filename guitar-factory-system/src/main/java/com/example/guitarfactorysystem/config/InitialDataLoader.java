package com.example.guitarfactorysystem.config;

import com.example.guitarfactorysystem.entity.GuitarComponent;
import com.example.guitarfactorysystem.entity.GuitarModel;
import com.example.guitarfactorysystem.enums.ComponentCategory;
import com.example.guitarfactorysystem.repository.GuitarComponentRepository;
import com.example.guitarfactorysystem.repository.GuitarModelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class InitialDataLoader {

    @Bean
    public CommandLineRunner loadInitialData(GuitarModelRepository guitarModelRepository,
                                             GuitarComponentRepository componentRepository) {
        return args -> {
            if (guitarModelRepository.count() == 0) {
                guitarModelRepository.saveAll(Arrays.asList(
                        buildModel("Strat Style", "Classic double-cut custom platform", "2500"),
                        buildModel("Tele Style", "Single-cut platform for versatile builds", "2300"),
                        buildModel("Les Paul Style", "Set-neck inspired premium platform", "3200")
                ));
            }

            if (componentRepository.count() == 0) {
                componentRepository.saveAll(Arrays.asList(
                        buildComponent("Alder Body", ComponentCategory.BODY_WOOD, "Alder", 10, "500"),
                        buildComponent("Maple Neck", ComponentCategory.NECK_WOOD, "Maple", 10, "700"),
                        buildComponent("Single Coil Pickup", ComponentCategory.PICKUP, "Single Coil", 30, "300"),
                        buildComponent("Humbucker Pickup", ComponentCategory.PICKUP, "Humbucker", 20, "450"),
                        buildComponent("Vintage Bridge", ComponentCategory.BRIDGE, "Vintage", 15, "350"),
                        buildComponent("Black Finish", ComponentCategory.FINISH, "Black", 20, "150"),
                        buildComponent("Standard Strings", ComponentCategory.STRINGS, "010-046", 50, "50")
                ));
            }
        };
    }

    private GuitarModel buildModel(String name, String description, String basePrice) {
        GuitarModel entity = new GuitarModel();
        entity.setName(name);
        entity.setDescription(description);
        entity.setBasePrice(new BigDecimal(basePrice));
        return entity;
    }

    private GuitarComponent buildComponent(String name,
                                           ComponentCategory category,
                                           String specificationValue,
                                           Integer quantityInStock,
                                           String unitPrice) {
        GuitarComponent entity = new GuitarComponent();
        entity.setName(name);
        entity.setCategory(category);
        entity.setSpecificationValue(specificationValue);
        entity.setQuantityInStock(quantityInStock);
        entity.setUnitPrice(new BigDecimal(unitPrice));
        return entity;
    }
}
