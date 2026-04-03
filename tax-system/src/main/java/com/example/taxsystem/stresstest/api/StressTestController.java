package com.example.taxsystem.stresstest.api;

import com.example.taxsystem.stresstest.application.StressTestService;
import com.example.taxsystem.stresstest.domain.StressExecutionStatus;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/stress-tests")
public class StressTestController {

    private final StressTestService stressTestService;

    public StressTestController(StressTestService stressTestService) {
        this.stressTestService = stressTestService;
    }

    @PostMapping
    public StressTestReport runSync(@Valid @RequestBody StressTestRequest request) {
        return stressTestService.runSync(request);
    }

    @PostMapping("/async")
    public ResponseEntity<StressAsyncResponse> runAsync(@Valid @RequestBody StressTestRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(stressTestService.runAsync(request));
    }

    @GetMapping("/reports/{executionId}")
    public StressExecutionStatus getReport(@PathVariable String executionId) {
        return stressTestService.getStatus(executionId);
    }
}
