package com.example.guitarfactorysystem.controller;

import com.example.guitarfactorysystem.dto.CreateWorkOrderRequest;
import com.example.guitarfactorysystem.dto.CreateWorkOrderResponse;
import com.example.guitarfactorysystem.dto.UpdateWorkOrderStatusRequest;
import com.example.guitarfactorysystem.dto.WorkOrderResponse;
import com.example.guitarfactorysystem.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderService service;

    public WorkOrderController(WorkOrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWorkOrderResponse create(@Valid @RequestBody CreateWorkOrderRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<WorkOrderResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public WorkOrderResponse get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}/status")
    public WorkOrderResponse updateStatus(@PathVariable("id") Long id,
                                          @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
        return service.updateStatus(id, request);
    }
}
