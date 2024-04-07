package com.example.instagramclone.services;

import com.example.instagramclone.dao.CommentDAO;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.Comment;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class CommentService {
    private final CommentDAO commentDAO;
    private final PostDAO postDAO;

    public CommentService(CommentDAO commentDAO, PostDAO postDAO) {
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
    }

    public void addComment(Post post, LocalUser user, String text) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setLocalUser(user);
        comment.setText(text);
        comment.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
        post.setCommentsCount(post.getCommentsCount() + 1);

        postDAO.save(post);
        commentDAO.save(comment);
    }

    public void deleteComment(Long id, Post post) {
        post.setCommentsCount(post.getCommentsCount() - 1);

        postDAO.save(post);
        commentDAO.deleteById(id);
    }

    public Comment getCommentById(Long id) {
        return commentDAO.findById(id).orElse(null);
    }
}
