package com.example.socialmediaphotoapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddCommentRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "text is required")
    private String text;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

