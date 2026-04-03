package com.example.restaurantqueuesystem.controller;

import com.example.restaurantqueuesystem.dto.QueueOrderResponse;
import com.example.restaurantqueuesystem.service.QueueEstimationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController {

    private final QueueEstimationService queueEstimationService;

    public QueueController(QueueEstimationService queueEstimationService) {
        this.queueEstimationService = queueEstimationService;
    }

    @GetMapping
    public List<QueueOrderResponse> getQueue() {
        return queueEstimationService.getQueue();
    }
}
