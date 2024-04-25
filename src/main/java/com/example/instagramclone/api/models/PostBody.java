package com.example.instagramclone.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostBody {
    @NotBlank(message = "dataUrl should not be null")
    @NotNull(message = "dataUrl should not be null")
    @Size(min = 5, message = "Data URL should be more than 5 characters")
    private String dataUrl;

    private String caption;

    private String location;

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
