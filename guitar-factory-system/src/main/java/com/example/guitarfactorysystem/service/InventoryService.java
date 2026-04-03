package com.example.guitarfactorysystem.service;

import com.example.guitarfactorysystem.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final ComponentService componentService;

    public InventoryService(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory() {
        return componentService.listInventory();
    }
}
