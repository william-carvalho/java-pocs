package com.example.socialmediaphotoapp.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;

public class AddTagsRequest {

    @NotEmpty(message = "tags is required")
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

