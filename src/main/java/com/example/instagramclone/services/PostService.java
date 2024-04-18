package com.example.instagramclone.services;

import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.api.models.PostResponse;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.dao.SubscriptionDAO;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.models.Subscription;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDAO postDAO;
    private final SubscriptionDAO subscriptionDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(PostDAO postDAO, SubscriptionDAO subscriptionDAO, ModelMapper modelMapper) {
        this.postDAO = postDAO;
        this.subscriptionDAO = subscriptionDAO;
        this.modelMapper = modelMapper;
    }

    public void createPost(PostBody postBody, LocalUser user) {
        Post post = new Post();

        post.setDataUrl(postBody.getDataUrl());
        post.setCaption(postBody.getCaption());
        post.setLocation(postBody.getLocation());
        post.setUser(user);
        post.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
        post.setCommentsCount(0);
        post.setLikesCount(0);

        postDAO.save(post);
    }

    public List<PostResponse> getAllPosts(LocalUser user) {
        List<Post> posts = postDAO.findByUserOrderByIdDesc(user);
        return posts.stream().map(this::mapToPostSummary).collect(Collectors.toList());
    }

    public List<PostResponse> findLastPostsOfFollowingUsers(LocalUser user) {
        List<Subscription> followings = subscriptionDAO.findByLocalUserOrderByIdDesc(user);
        List<Post> posts = new ArrayList<>();

        for (Subscription sub : followings) {
            posts.addAll(postDAO.findTop10ByUserOrderByIdDesc(sub.getFollowingUser()));
        }

        posts.addAll(postDAO.findTop10ByUserOrderByIdDesc(user));
        posts.sort((Comparator.comparing(Post::getCreationTimestamp).reversed()));

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
