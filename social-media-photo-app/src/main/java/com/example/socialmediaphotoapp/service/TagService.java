package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.entity.Tag;
import com.example.socialmediaphotoapp.repository.TagRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> resolvedTags = new LinkedHashSet<Tag>();

        for (String tagName : tagNames) {
            String normalized = normalize(tagName);
            Tag tag = tagRepository.findByNameIgnoreCase(normalized)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(normalized);
                        return tagRepository.save(newTag);
                    });
            resolvedTags.add(tag);
        }

        return resolvedTags;
    }

    public List<String> toNames(Set<Tag> tags) {
        return tags.stream().map(Tag::getName).sorted().collect(Collectors.toList());
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("tag must not be empty");
        }
        return value.trim().toLowerCase();
    }
}

