package com.example.restaurantqueuesystem.controller;

import com.example.restaurantqueuesystem.dto.CreateOrderRequest;
import com.example.restaurantqueuesystem.dto.OrderEstimateResponse;
import com.example.restaurantqueuesystem.dto.OrderResponse;
import com.example.restaurantqueuesystem.dto.UpdateOrderStatusRequest;
import com.example.restaurantqueuesystem.service.OrderService;
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

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable("id") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable("id") Long orderId,
                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(orderId, request);
    }

    @GetMapping("/{id}/estimate")
    public OrderEstimateResponse getEstimate(@PathVariable("id") Long orderId) {
        return orderService.getEstimate(orderId);
    }
}
