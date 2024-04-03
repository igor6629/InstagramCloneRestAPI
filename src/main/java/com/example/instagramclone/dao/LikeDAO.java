package com.example.instagramclone.dao;

import com.example.instagramclone.models.LikeCount;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeDAO extends JpaRepository<LikeCount, Long> {
    Optional<LikeCount> findByPostAndLocalUser(Post post, LocalUser localUser);
}
