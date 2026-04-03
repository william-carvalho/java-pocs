package com.example.hibernateslowquerydetector.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hibernateslowquerydetector.dto.SlowQueryConfigResponse;
import com.example.hibernateslowquerydetector.dto.SlowQueryResponse;
import com.example.hibernateslowquerydetector.dto.SlowQueryStatsResponse;
import com.example.hibernateslowquerydetector.service.SlowQueryService;

@RestController
@RequestMapping("/slow-queries")
public class SlowQueryController {

    private final SlowQueryService slowQueryService;

    public SlowQueryController(SlowQueryService slowQueryService) {
        this.slowQueryService = slowQueryService;
    }

    @GetMapping
    public List<SlowQueryResponse> listAll() {
        return slowQueryService.listAll();
    }

    @GetMapping("/{id}")
    public SlowQueryResponse getById(@PathVariable Long id) {
        return slowQueryService.getById(id);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearAll() {
        slowQueryService.clearAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public SlowQueryStatsResponse getStats() {
        return slowQueryService.getStats();
    }

    @GetMapping("/config")
    public SlowQueryConfigResponse getConfig() {
        return slowQueryService.getConfig();
    }
}
