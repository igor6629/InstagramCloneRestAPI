package com.example.instagramclone.services;

import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class PostService {

    private final PostDAO postDAO;

    public PostService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    public void createPost(PostBody postBody, LocalUser user) {
        Post post = new Post();

        post.setDataUrl(postBody.getDataUrl());
        post.setCaption(postBody.getCaption());
        post.setLocation(postBody.getLocation());
        post.setUser(user);
        post.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));

        postDAO.save(post);
    }

    public List<Post> getAllPosts(LocalUser user) {
        return postDAO.findByUserOrderByIdDesc(user);
    }

    public Post getPostById(long id) {
        return postDAO.findById(id).orElse(null);
    }

    public void deletePostById(Long id) {
        postDAO.deleteById(id);
    }
}
