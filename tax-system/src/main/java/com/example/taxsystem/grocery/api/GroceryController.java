package com.example.taxsystem.grocery.api;

import com.example.taxsystem.grocery.application.GroceryService;
import com.example.taxsystem.grocery.domain.GroceryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grocery/items")
public class GroceryController {

    private final GroceryService groceryService;

    public GroceryController(GroceryService groceryService) {
        this.groceryService = groceryService;
    }

    @PostMapping
    public ResponseEntity<GroceryItemResponse> create(@Valid @RequestBody CreateGroceryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groceryService.create(request));
    }

    @GetMapping
    public List<GroceryItemResponse> list(@RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "status", required = false) GroceryStatus status) {
        return groceryService.list(name, status);
    }

    @PutMapping("/{itemId}/done")
    public GroceryItemResponse markDone(@PathVariable Long itemId) {
        return groceryService.markDone(itemId);
    }

    @PutMapping("/{itemId}/do")
    public GroceryItemResponse doItem(@PathVariable Long itemId) {
        return groceryService.doItem(itemId);
    }

    @PutMapping("/{itemId}/redo")
    public GroceryItemResponse redo(@PathVariable Long itemId) {
        return groceryService.redo(itemId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long itemId) {
        groceryService.remove(itemId);
    }
}
