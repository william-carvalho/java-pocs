package com.example.taxsystem.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taxsystem.dto.TaxCalculationRequest;
import com.example.taxsystem.dto.TaxCalculationResponse;
import com.example.taxsystem.service.TaxService;

@RestController
@RequestMapping("/tax")
public class TaxController {

    private final TaxService taxService;

    public TaxController(TaxService taxService) {
        this.taxService = taxService;
    }

    @PostMapping("/calculate")
    public TaxCalculationResponse calculate(@Valid @RequestBody TaxCalculationRequest request) {
        return taxService.calculate(request);
    }
}
