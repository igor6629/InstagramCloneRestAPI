package com.example.instagramclone.dao;

import com.example.instagramclone.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDAO extends JpaRepository<Comment, Long> {
}
