package com.example.instagramclone.api.models;

import com.example.instagramclone.models.LocalUser;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class PostResponse {
    private Long id;
    private String dataUrl;
    private String caption;
    private Timestamp creationTimestamp;
    private String location;
    private LocalUser user;
    private Integer likesCount;
    private Integer commentsCount;

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
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

    public LocalUser getUser() {
        return user;
    }

    public void setUser(LocalUser localUser) {
        this.user = localUser;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    public String getUsername() {
        return user.getUsername();
    }
}
