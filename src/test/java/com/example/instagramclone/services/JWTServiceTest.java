package com.example.instagramclone.services;

import com.example.instagramclone.dao.UserDAO;
import com.example.instagramclone.models.LocalUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDAO userDAO;

    @Test
    public void testAuthTokenReturnsUsername() {
        LocalUser user = userDAO.findByUsername("UserA").orElse(null);
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should contain username of user");
    }
}
