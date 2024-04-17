package com.example.instagramclone.services;

import com.example.instagramclone.api.models.LoginBody;
import com.example.instagramclone.api.models.RegistrationBody;
import com.example.instagramclone.dao.UserDAO;
import com.example.instagramclone.exceptions.UserAlreadyExistException;
import com.example.instagramclone.models.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;

    @Autowired
    public UserService(UserDAO userDAO, EncryptionService encryptionService, JWTService jwtService) {
        this.userDAO = userDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }


    public LocalUser saveUser(RegistrationBody registrationBody) throws UserAlreadyExistException {

        if (userDAO.findByUsername(registrationBody.getUsername()).isPresent()
                || userDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistException();
        }

        LocalUser user = new LocalUser();

        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setBio(registrationBody.getBio());
        user.setProfilePicture(registrationBody.getProfilePicture());
        user.setFollowing(0);
        user.setFollowers(0);

        return userDAO.save(user);
    }

    public String loginUser(LoginBody loginBody) {

        LocalUser user = userDAO.findByUsername(loginBody.getUsername()).orElse(null);

        if (user != null && encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword()))
            return jwtService.generateJWT(user);

        return null;
    }

    public Optional<LocalUser> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}
