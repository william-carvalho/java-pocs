package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.SellTicketRequest;
import com.example.ticketsystem.dto.SellTicketResponse;
import com.example.ticketsystem.dto.TicketResponse;
import com.example.ticketsystem.enums.TicketStatus;
import com.example.ticketsystem.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.CREATED)
    public SellTicketResponse sell(@Valid @RequestBody SellTicketRequest request) {
        return service.sell(request);
    }

    @GetMapping
    public List<TicketResponse> list(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) TicketStatus status) {
        return service.list(sessionId, zoneId, status);
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}/cancel")
    public TicketResponse cancel(@PathVariable("id") Long id) {
        return service.cancel(id);
    }
}
