package com.example.instagramclone.dao;

import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDAO extends JpaRepository<Post, Long> {
    List<Post> findByUser(LocalUser user);

    List<Post> findByUserOrderByIdDesc(LocalUser user);
}
