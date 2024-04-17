package com.example.instagramclone.models;

import jakarta.persistence.*;

@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "local_user_id")
    private LocalUser localUser;

    @ManyToOne
    @JoinColumn(name = "following_user_id")
    private LocalUser followingUser;

    public LocalUser getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(LocalUser followingUser) {
        this.followingUser = followingUser;
    }

    public LocalUser getLocalUser() {
        return localUser;
    }

    public void setLocalUser(LocalUser localUser) {
        this.localUser = localUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
