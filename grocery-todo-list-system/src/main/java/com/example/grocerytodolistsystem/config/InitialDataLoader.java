package com.example.grocerytodolistsystem.config;

import com.example.grocerytodolistsystem.entity.GroceryItem;
import com.example.grocerytodolistsystem.enums.ItemStatus;
import com.example.grocerytodolistsystem.repository.GroceryItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class InitialDataLoader {

    @Bean
    public CommandLineRunner loadInitialData(GroceryItemRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.saveAll(Arrays.asList(
                    buildItem("Milk", "2 liters", ItemStatus.PENDING),
                    buildItem("Bread", "1 package", ItemStatus.PENDING),
                    buildItem("Eggs", "12 units", ItemStatus.DONE)
            ));
        };
    }

    private GroceryItem buildItem(String name, String quantity, ItemStatus status) {
        GroceryItem item = new GroceryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setStatus(status);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(item.getCreatedAt());
        return item;
    }
}
