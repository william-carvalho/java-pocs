package com.example.logisticfreightsystem.controller;

import com.example.logisticfreightsystem.dto.FreightPricingRuleRequest;
import com.example.logisticfreightsystem.dto.FreightPricingRuleResponse;
import com.example.logisticfreightsystem.enums.TransportType;
import com.example.logisticfreightsystem.service.FreightPricingRuleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/freight")
public class FreightRuleController {

    private final FreightPricingRuleService freightPricingRuleService;

    public FreightRuleController(FreightPricingRuleService freightPricingRuleService) {
        this.freightPricingRuleService = freightPricingRuleService;
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public FreightPricingRuleResponse createRule(@Valid @RequestBody FreightPricingRuleRequest request) {
        return freightPricingRuleService.createRule(request);
    }

    @GetMapping("/rules")
    public List<FreightPricingRuleResponse> listRules(
            @RequestParam(required = false) TransportType transportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate) {
        return freightPricingRuleService.listRules(transportType, referenceDate);
    }

    @GetMapping("/transport-types")
    public List<TransportType> listTransportTypes() {
        return Arrays.asList(TransportType.values());
    }
}
