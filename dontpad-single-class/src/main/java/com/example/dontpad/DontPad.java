package com.example.dontpad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DontPad {
    private static final String SLUG_PATTERN = "[a-zA-Z0-9_-]+";

    private final Map<String, Pad> padsBySlug = new LinkedHashMap<String, Pad>();
    private final TimeSource timeSource;

    public DontPad() {
        this(new TimeSource() {
            public LocalDateTime now() {
                return LocalDateTime.now();
            }
        });
    }

    public DontPad(TimeSource timeSource) {
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource is required");
        }
        this.timeSource = timeSource;
    }

    public synchronized Pad createPad(String slug, String content) {
        String normalizedSlug = normalizeSlug(slug);
        if (padsBySlug.containsKey(normalizedSlug)) {
            throw new IllegalStateException("Pad already exists: " + normalizedSlug);
        }

        LocalDateTime now = timeSource.now();
        Pad pad = new Pad(normalizedSlug, content, now);
        padsBySlug.put(normalizedSlug, pad);
        return pad;
    }

    public synchronized Pad getOrCreate(String slug) {
        String normalizedSlug = normalizeSlug(slug);
        Pad existing = padsBySlug.get(normalizedSlug);
        if (existing != null) {
            return existing;
        }
        return createPad(normalizedSlug, "");
    }

    public synchronized Pad updatePad(String slug, String content) {
        Pad pad = getOrCreate(slug);
        pad.update(content, timeSource.now());
        return pad;
    }

    public synchronized Pad deletePad(String slug) {
        String normalizedSlug = normalizeSlug(slug);
        Pad removed = padsBySlug.remove(normalizedSlug);
        if (removed == null) {
            throw new IllegalArgumentException("Pad not found: " + normalizedSlug);
        }
        return removed;
    }

    public synchronized List<PadSummary> listPads() {
        List<PadSummary> summaries = new ArrayList<PadSummary>();
        for (Pad pad : padsBySlug.values()) {
            summaries.add(new PadSummary(
                    pad.getSlug(),
                    pad.getContent().length(),
                    pad.getVersion(),
                    pad.getCreatedAt(),
                    pad.getUpdatedAt()));
        }
        return Collections.unmodifiableList(summaries);
    }

    public synchronized boolean exists(String slug) {
        return padsBySlug.containsKey(normalizeSlug(slug));
    }

    public synchronized int count() {
        return padsBySlug.size();
    }

    public static DontPad withWelcomePad() {
        DontPad dontPad = new DontPad();
        dontPad.createPad("welcome", "Welcome to DontPad Clone");
        return dontPad;
    }

    private static String normalizeSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("slug is required");
        }
        String normalizedSlug = slug.trim();
        if (!normalizedSlug.matches(SLUG_PATTERN)) {
            throw new IllegalArgumentException("slug must match " + SLUG_PATTERN);
        }
        return normalizedSlug;
    }

    public interface TimeSource {
        LocalDateTime now();
    }

    public static final class Pad {
        private final String slug;
        private String content;
        private int version;
        private final LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Pad(String slug, String content, LocalDateTime now) {
            this.slug = slug;
            this.content = content == null ? "" : content;
            this.version = 1;
            this.createdAt = now;
            this.updatedAt = now;
        }

        private void update(String content, LocalDateTime now) {
            this.content = content == null ? "" : content;
            this.version++;
            this.updatedAt = now;
        }

        public String getSlug() {
            return slug;
        }

        public String getContent() {
            return content;
        }

        public int getVersion() {
            return version;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }

    public static final class PadSummary {
        private final String slug;
        private final int contentLength;
        private final int version;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private PadSummary(String slug, int contentLength, int version, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.slug = slug;
            this.contentLength = contentLength;
            this.version = version;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getSlug() {
            return slug;
        }

        public int getContentLength() {
            return contentLength;
        }

        public int getVersion() {
            return version;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }
}
