package com.example.logisticfreightsystem.controller;

import com.example.logisticfreightsystem.dto.FreightCalculationRequest;
import com.example.logisticfreightsystem.dto.FreightCalculationResponse;
import com.example.logisticfreightsystem.service.FreightCalculationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/freight")
public class FreightCalculationController {

    private final FreightCalculationService freightCalculationService;

    public FreightCalculationController(FreightCalculationService freightCalculationService) {
        this.freightCalculationService = freightCalculationService;
    }

    @PostMapping("/calculate")
    public FreightCalculationResponse calculate(@Valid @RequestBody FreightCalculationRequest request) {
        return freightCalculationService.calculate(request);
    }
}
