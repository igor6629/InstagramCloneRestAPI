package com.example.instagramclone.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentBody {
    @NotBlank
    @NotNull(message = "text should not be empty")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
