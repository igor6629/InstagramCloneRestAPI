package com.example.instagramclone.services;

import com.example.instagramclone.dao.LikeDAO;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.LikeCount;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class LikeService {
    private final LikeDAO likeDAO;
    private final PostDAO postDAO;

    public LikeService(LikeDAO likeDAO, PostDAO postDAO) {
        this.likeDAO = likeDAO;
        this.postDAO = postDAO;
    }

    public void makeLike(LocalUser user, Post post) {
        if (likeDAO.findByPostAndLocalUser(post, user).isPresent())
            return;

        post.setLikesCount(post.getLikesCount() + 1);

        LikeCount like = new LikeCount();
        like.setLocalUser(user);
        like.setPost(post);
        like.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));

        likeDAO.save(like);
        postDAO.save(post);
    }

    public void makeUnlike(LocalUser user, Long postId, Post post) {
        LikeCount like = likeDAO.findByPostAndLocalUser(post, user).orElse(null);

        if (like != null) {
            post.setLikesCount(post.getLikesCount() - 1);
            likeDAO.delete(like);
            postDAO.save(post);
        }
    }
}
