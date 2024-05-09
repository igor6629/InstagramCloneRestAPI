package com.example.instagramclone.services;

import com.example.instagramclone.api.models.LoginBody;
import com.example.instagramclone.api.models.RegistrationBody;
import com.example.instagramclone.exceptions.UserAlreadyExistException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRegistrationUser() {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setPassword("MySecretPassword123");
        body.setBio("UserA");
        body.setProfilePicture("google.com/userA");

        Assertions.assertThrows(UserAlreadyExistException.class,
                () -> userService.saveUser(body), "username should already be in use.");

        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");

        Assertions.assertThrows(UserAlreadyExistException.class,
                () -> userService.saveUser(body), "email should already be in use.");

        body.setEmail("UserServiceTest$testRegisterUser@junit.com");

        Assertions.assertDoesNotThrow(() -> userService.saveUser(body), "user should register successfully.");
    }

    @Test
    @Transactional
    public void testLoginUser() {
        LoginBody user = new LoginBody();
        user.setUsername("UserA-Non-Exists");
        user.setPassword("PasswordA123-BadPassword");

        Assertions.assertNull(userService.loginUser(user), "The user should not exist");

        user.setUsername("UserA");

        Assertions.assertNull(userService.loginUser(user), "The password should be incorrect");

        user.setPassword("PasswordA123");

        Assertions.assertNotNull(userService.loginUser(user), "The user should login successfully");
    }

    @Test
    @Transactional
    public void testGetUserByUsername() {
        String username = "UserA-Non-Exists";

        Assertions.assertNull(userService.getUserByUsername(username), "The user should not exists");

        username = "UserA";

        Assertions.assertNotNull(userService.getUserByUsername(username), "The user should be found");
    }
}
