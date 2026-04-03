package com.example.grocerytodolistsystem.controller;

import com.example.grocerytodolistsystem.dto.GroceryItemRequest;
import com.example.grocerytodolistsystem.dto.GroceryItemResponse;
import com.example.grocerytodolistsystem.enums.ItemStatus;
import com.example.grocerytodolistsystem.service.GroceryItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class GroceryItemController {

    private final GroceryItemService service;

    public GroceryItemController(GroceryItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroceryItemResponse create(@Valid @RequestBody GroceryItemRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<GroceryItemResponse> listAll(@RequestParam(required = false) ItemStatus status) {
        return service.listAll(status);
    }

    @GetMapping("/{id}")
    public GroceryItemResponse getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/done")
    public GroceryItemResponse markDone(@PathVariable("id") Long id) {
        return service.markDone(id);
    }

    @PatchMapping("/{id}/do")
    public GroceryItemResponse markDo(@PathVariable("id") Long id) {
        return service.markDo(id);
    }

    @PatchMapping("/{id}/redo")
    public GroceryItemResponse markRedo(@PathVariable("id") Long id) {
        return service.markRedo(id);
    }
}
