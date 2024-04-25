package com.example.instagramclone.api.controllers.auth;

import com.example.instagramclone.api.models.LoginBody;
import com.example.instagramclone.api.models.LoginResponse;
import com.example.instagramclone.api.models.RegistrationBody;
import com.example.instagramclone.exceptions.UserAlreadyExistException;
import com.example.instagramclone.services.UserService;
import com.example.instagramclone.util.Util;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> registration(@Valid @RequestBody RegistrationBody registrationBody,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return Util.getErrors(bindingResult);

        try {
            userService.saveUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new HashMap<>(){{put("Error:", "User with this username or email is already exist");}}
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginBody loginBody) {
        String jwt = userService.loginUser(loginBody);

        if (jwt == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);

        return ResponseEntity.ok(response);
    }
}
