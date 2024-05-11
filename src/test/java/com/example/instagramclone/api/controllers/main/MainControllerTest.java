package com.example.instagramclone.api.controllers.main;

import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.services.JWTService;
import com.example.instagramclone.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Test
    public void testLastPostsOfFollowingUsers() throws Exception {
        LocalUser user = userService.getUserByUsername("UserA");
        String token = jwtService.generateJWT(user);

        // not authenticated user
        mvc.perform(get("")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // valid response
        mvc.perform(get("")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
