package com.example.instagramclone.api.controllers.auth;

import com.example.instagramclone.api.models.LoginBody;
import com.example.instagramclone.api.models.RegistrationBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    public void testRegistration() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RegistrationBody body = new RegistrationBody();

        body.setEmail("newuseremail@junit.com");
        body.setPassword("Password123");
        body.setProfilePicture("google.com/newuser");
        body.setBio("New user.");

        // Test NULL or Blank username
        body.setUsername(null);

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUsername("");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test less than 3 or more than 20 characters username
        body.setUsername("us");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUsername("newuser1234newuser1234");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUsername("newuser123");

        // Test NULL or Blank or Invalid email
        body.setEmail(null);

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setEmail("");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setEmail("invalidemail.com");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setEmail("newuseremail@junit.com");

        // Test NULL or Blank or Invalid password
        body.setPassword(null);

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("invalidpassword");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("Password123");

        // Test NULL or Blank or incorrect profilePicture
        body.setProfilePicture(null);

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setProfilePicture("");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Profile picture URL should be more than 5 characters
        body.setProfilePicture("data");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setProfilePicture("google.com/newuser");

        // Catch user already exist
        body.setUsername("UserA");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));

        body.setUsername("newuser123");

        body.setEmail("UserA@junit.com");

        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));

        body.setEmail("newuseremail@junit.com");

        // Correct registration
        mvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @Transactional
    public void testLogin() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LoginBody body = new LoginBody();

        body.setPassword("PasswordA123");

        // Test NULL or Blank or Non-Exists username
        body.setUsername("UserA-Non-Exist");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUsername("UserA");

        // Test NULL or Blank or Incorrect password
        body.setPassword("NonCorrectPass123");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("PasswordA123");

        // Correct login
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
