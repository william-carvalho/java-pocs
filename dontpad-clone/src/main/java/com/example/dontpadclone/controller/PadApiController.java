package com.example.dontpadclone.controller;

import com.example.dontpadclone.dto.CreatePadRequest;
import com.example.dontpadclone.dto.PadResponse;
import com.example.dontpadclone.dto.PadSummaryResponse;
import com.example.dontpadclone.dto.UpdatePadRequest;
import com.example.dontpadclone.service.PadService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pads")
public class PadApiController {

    private final PadService padService;

    public PadApiController(PadService padService) {
        this.padService = padService;
    }

    @GetMapping("/{slug}")
    public PadResponse getOrCreate(@PathVariable String slug) {
        return padService.getOrCreateBySlug(slug);
    }

    @PutMapping("/{slug}")
    public PadResponse upsert(@PathVariable String slug, @RequestBody(required = false) UpdatePadRequest request) {
        return padService.upsertBySlug(slug, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PadResponse create(@Valid @RequestBody CreatePadRequest request) {
        return padService.create(request);
    }

    @GetMapping
    public List<PadSummaryResponse> listAll() {
        return padService.listAll();
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String slug) {
        padService.deleteBySlug(slug);
    }
}

