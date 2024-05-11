package com.example.instagramclone.api.controllers.user;

import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.services.JWTService;
import com.example.instagramclone.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    JWTService jwtService;

    @Test
    public void testFollowUser() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // the same username
        mvc.perform(put("/user/UserA/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // not exist user
        mvc.perform(put("/user/NonExistUser/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(put("/user/UserB/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testUnfollowUser() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // the same username
        mvc.perform(put("/user/UserA/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // not exist user
        mvc.perform(put("/user/NonExistUser/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(put("/user/UserB/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testGetUserProfile() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not exist user
        mvc.perform(get("/user/NonExistUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(get("/user/UserA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    LocalUser currentUser = new ObjectMapper().readValue(json, new TypeReference<LocalUser>() {});
                    Assertions.assertEquals(user.getUsername(), currentUser.getUsername());
                });
    }

    @Test
    public void testGetUserFollowing() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not exist user
        mvc.perform(get("/user/NonExistUser/following")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(get("/user/UserA/following")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testGetUserFollowers() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not exist user
        mvc.perform(get("/user/NonExistUser/followers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(get("/user/UserA/followers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testGetUserMutualFollowers() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // the same user
        mvc.perform(get("/user/UserA/mutual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // not exist user
        mvc.perform(get("/user/NonExistUser/mutual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // valid following
        mvc.perform(get("/user/UserB/mutual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
