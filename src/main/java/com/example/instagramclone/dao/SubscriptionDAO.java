package com.example.instagramclone.dao;

import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionDAO extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByLocalUserAndFollowingUser(LocalUser localUser, LocalUser followingUser);
    List<Subscription> findByLocalUserOrderByIdDesc(LocalUser localUser);
    List<Subscription> findByFollowingUserOrderByIdDesc(LocalUser followingUser);
    boolean existsByLocalUserAndFollowingUser(LocalUser localUser, LocalUser followingUser);
}
