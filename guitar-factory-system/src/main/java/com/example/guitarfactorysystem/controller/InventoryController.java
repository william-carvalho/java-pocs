package com.example.guitarfactorysystem.controller;

import com.example.guitarfactorysystem.dto.InventoryResponse;
import com.example.guitarfactorysystem.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<InventoryResponse> list() {
        return service.listInventory();
    }
}
