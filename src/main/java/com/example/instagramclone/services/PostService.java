package com.example.instagramclone.services;

import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.api.models.PostResponse;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDAO postDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(PostDAO postDAO, ModelMapper modelMapper) {
        this.postDAO = postDAO;
        this.modelMapper = modelMapper;
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

    public List<PostResponse> getAllPosts(LocalUser user) {
        List<Post> posts = postDAO.findByUserOrderByIdDesc(user);
        return posts.stream().map(this::mapToPostSummary).collect(Collectors.toList());
    }

    public Post getPostById(long id) {
        return postDAO.findById(id).orElse(null);
    }

    public void deletePostById(Long id) {
        postDAO.deleteById(id);
    }

    public PostResponse mapToPostSummary(Post post) {
        return modelMapper.map(post, PostResponse.class);
    }
}
