package com.example.dualwrite.api;

import com.example.dualwrite.broker.BrokerUnavailableException;
import com.example.dualwrite.service.DemoFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({BrokerUnavailableException.class, DemoFailureException.class})
    public ResponseEntity<Map<String, Object>> handleDemonstratedFailure(RuntimeException exception) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Falha intencional da POC");
        body.put("message", exception.getMessage());
        body.put("nextStep", "Consulte GET /api/state para observar a inconsistencia resultante.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
