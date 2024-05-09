package com.example.instagramclone.api.controllers.post;


import com.example.instagramclone.api.models.CommentBody;
import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.api.models.PostResponse;
import com.example.instagramclone.api.models.UpdatePostBody;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.services.JWTService;
import com.example.instagramclone.services.PostService;
import com.example.instagramclone.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private JWTService jwtService;


    @Test
    @Transactional
    public void testAddPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LocalUser user =  userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);
        PostBody body = new PostBody();

        // Test Unauthorized user
        mvc.perform(post("/post/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // Test Invalid PostBody
        body.setDataUrl("bad");

        mvc.perform(post("/post/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setDataUrl("google.com/adding-new-post");
        body.setLocation("Los Angeles");
        body.setCaption("New post");

        // Correct adding new post
        mvc.perform(post("/post/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @WithMockUser
    public void testUserAAuthenticatedPostList() throws Exception {
        testGetPosts("UserA");
    }

    @Test
    @WithMockUser
    public void testUserBAuthenticatedPostList() throws Exception {
        testGetPosts("UserB");
    }

    public void testGetPosts(String username) throws Exception {
        mvc.perform(get("/post/" + username + "/show")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<PostResponse> posts = new ObjectMapper().readValue(json, new TypeReference<List<PostResponse>>() {});
                    for (PostResponse post : posts) {
                        Assertions.assertEquals(username, post.getUsername(), "Post list should only be belonging to the user.");
                    }
                });
    }

    @Test
    public void testUnauthenticatedUserGetPosts() throws Exception {
        mvc.perform(get("/post/UserA/show")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser
    public void testGetPostsNonExistUser() throws Exception {
        mvc.perform(get("/post/UserNonExist/show")).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testGetPostById() throws Exception {
        LocalUser user =  userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        //  not authorised user
        mvc.perform(get("/post/" + 1)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // Test non exist post
        mvc.perform(get("/post/" + 7).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test valid post
        mvc.perform(get("/post/" + 1).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Post post = new ObjectMapper().readValue(json, new TypeReference<Post>() {});
                    Assertions.assertEquals(1, post.getId(), "Post should only be belonging to the user.");
                });
    }

    @Test
    public void testUnauthenticatedUserGetPostById() throws Exception {
        mvc.perform(get("/post/" + 1)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testDeletePostById() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not exist post
        mvc.perform(delete("/post/25").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // no-own post
        mvc.perform(delete("/post/4").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // success deleting
        mvc.perform(delete("/post/1").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));

        // trying to find post
        mvc.perform(get("/post/" + 1).with(user("UserA"))).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testUpdatePost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);
        UpdatePostBody post = new UpdatePostBody();
        post.setCaption("Updated post");
        post.setLocation("Oslo, Norway");

        // not exist post
        mvc.perform(patch("/post/25/update").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(post))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // no-own post
        mvc.perform(patch("/post/4/update").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(post))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // valid updating
        mvc.perform(patch("/post/1/update").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(post))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));

        Post updatedPost = postService.getPostById(1);
        Assertions.assertEquals(post.getCaption(), updatedPost.getCaption(), "Posts caption should be the same.");
    }

    @Test
    public void testLikePostById() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not authenticated user
        mvc.perform(put("/post/4/like").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // not exist post
        mvc.perform(put("/post/25/like").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid post
        mvc.perform(put("/post/4/like").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testUnlikePostById() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not authenticated user
        mvc.perform(put("/post/4/unlike").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // not exist post
        mvc.perform(put("/post/25/unlike").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid post
        mvc.perform(put("/post/4/unlike").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testAddComment() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);
        CommentBody comment = new CommentBody();

        // not authenticated user
        mvc.perform(patch("/post/4/comment").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // invalid CommentBody
        comment.setComment(null);
        mvc.perform(patch("/post/4/comment").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        comment.setComment("Wow, nice picture!");

        // not exist post
        mvc.perform(patch("/post/25/comment").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid adding comment
        mvc.perform(patch("/post/4/comment").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testDeleteComment() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not authenticated user
        mvc.perform(delete("/post/4/comment/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // not exist post
        mvc.perform(delete("/post/25/comment/1").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // not exist comment
        mvc.perform(delete("/post/4/comment/25").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // no-own comment or post
        LocalUser noOwnUser = userService.getUserByUsername("UserC");
        String noOwnToken = jwtService.generateJWT(noOwnUser);

        mvc.perform(delete("/post/4/comment/1").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + noOwnToken))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // comment for another post
        mvc.perform(delete("/post/4/comment/3").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid deleting comment as user who added comment
        mvc.perform(delete("/post/4/comment/1").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testDeleteCommentAsPostOwner() throws Exception {
        LocalUser user = userService.getUserByUsername("UserB");
        String token = jwtService.generateJWT(user);

        mvc.perform(delete("/post/4/comment/2").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
