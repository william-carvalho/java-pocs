package com.example.dontpadclone.service;

import com.example.dontpadclone.dto.CreatePadRequest;
import com.example.dontpadclone.dto.PadResponse;
import com.example.dontpadclone.dto.PadSummaryResponse;
import com.example.dontpadclone.dto.UpdatePadRequest;
import com.example.dontpadclone.entity.Pad;
import com.example.dontpadclone.exception.BusinessException;
import com.example.dontpadclone.exception.NotFoundException;
import com.example.dontpadclone.repository.PadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PadService {

    private static final Pattern SLUG_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

    private final PadRepository padRepository;

    public PadService(PadRepository padRepository) {
        this.padRepository = padRepository;
    }

    @Transactional
    public PadResponse create(CreatePadRequest request) {
        String slug = normalizeAndValidateSlug(request.getSlug());
        if (padRepository.existsBySlug(slug)) {
            throw new BusinessException("Pad slug already exists: " + slug);
        }

        Pad pad = new Pad();
        pad.setSlug(slug);
        pad.setContent(normalizeContent(request.getContent()));
        return PadResponse.from(padRepository.save(pad));
    }

    @Transactional
    public PadResponse getOrCreateBySlug(String rawSlug) {
        String slug = normalizeAndValidateSlug(rawSlug);
        Pad pad = padRepository.findBySlug(slug).orElseGet(() -> {
            Pad created = new Pad();
            created.setSlug(slug);
            created.setContent("");
            return padRepository.save(created);
        });
        return PadResponse.from(pad);
    }

    @Transactional
    public PadResponse upsertBySlug(String rawSlug, UpdatePadRequest request) {
        String slug = normalizeAndValidateSlug(rawSlug);
        Pad pad = padRepository.findBySlug(slug).orElseGet(() -> {
            Pad created = new Pad();
            created.setSlug(slug);
            created.setContent("");
            return created;
        });
        pad.setContent(normalizeContent(request == null ? null : request.getContent()));
        return PadResponse.from(padRepository.save(pad));
    }

    @Transactional(readOnly = true)
    public List<PadSummaryResponse> listAll() {
        return padRepository.findAll().stream()
                .map(PadSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBySlug(String rawSlug) {
        String slug = normalizeAndValidateSlug(rawSlug);
        if (!padRepository.existsBySlug(slug)) {
            throw new NotFoundException("Pad not found: " + slug);
        }
        padRepository.deleteBySlug(slug);
    }

    private String normalizeAndValidateSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new BusinessException("Slug is required");
        }
        String normalized = slug.trim();
        if (!SLUG_PATTERN.matcher(normalized).matches()) {
            throw new BusinessException("Slug must match [a-zA-Z0-9_-]+");
        }
        return normalized;
    }

    private String normalizeContent(String content) {
        return content == null ? "" : content;
    }
}

