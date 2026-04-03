package com.example.hibernateslowquerydetector.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hibernateslowquerydetector.dto.DemoQueryResponse;
import com.example.hibernateslowquerydetector.service.DemoDataLoaderService;
import com.example.hibernateslowquerydetector.service.HibernateQueryExecutionService;

@RestController
@RequestMapping("/demo")
public class DemoQueryController {

    private final DemoDataLoaderService demoDataLoaderService;
    private final HibernateQueryExecutionService hibernateQueryExecutionService;

    public DemoQueryController(DemoDataLoaderService demoDataLoaderService,
                               HibernateQueryExecutionService hibernateQueryExecutionService) {
        this.demoDataLoaderService = demoDataLoaderService;
        this.hibernateQueryExecutionService = hibernateQueryExecutionService;
    }

    @GetMapping("/load-sample-data")
    public DemoQueryResponse loadSampleData() {
        return new DemoQueryResponse(demoDataLoaderService.loadSampleData(), null);
    }

    @GetMapping("/run-fast-query")
    public DemoQueryResponse runFastQuery() {
        return new DemoQueryResponse("Fast query finished", hibernateQueryExecutionService.runFastQuery());
    }

    @GetMapping("/run-slow-query")
    public DemoQueryResponse runSlowQuery() {
        return new DemoQueryResponse("Slow query finished", hibernateQueryExecutionService.runSlowQuery());
    }
}
