package com.example.instagramclone.api.controllers.user;

import com.example.instagramclone.api.models.SubscriptionResponse;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.services.SubscriptionService;
import com.example.instagramclone.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public UserController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @PutMapping("/{username}/follow")
    public ResponseEntity<HttpStatus> followUser(@AuthenticationPrincipal LocalUser user,
                                                   @PathVariable("username") String username) {
        if (Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        LocalUser followingUser = userService.getUserByUsername(username);

        if (followingUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        subscriptionService.follow(user, followingUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/unfollow")
    public ResponseEntity<HttpStatus> unfollowUser(@AuthenticationPrincipal LocalUser user,
                                                   @PathVariable("username") String username) {
        if (Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        LocalUser followingUser = userService.getUserByUsername(username);

        if (followingUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        subscriptionService.unfollow(user, followingUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<LocalUser> getUserProfile(@AuthenticationPrincipal LocalUser localUser,
                                                     @PathVariable("username") String username) {
        LocalUser user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<SubscriptionResponse>> getUserFollowing(@AuthenticationPrincipal LocalUser localUser,
                                                                       @PathVariable("username") String username) {
        LocalUser user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(subscriptionService.getFollowings(user));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<SubscriptionResponse>> getUserFollowers(@AuthenticationPrincipal LocalUser localUser,
                                                                       @PathVariable("username") String username) {
        LocalUser user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(subscriptionService.getFollowers(user));
    }

    @GetMapping("/{username}/mutual")
    public ResponseEntity<List<SubscriptionResponse>> getUserMutualFollowers(@AuthenticationPrincipal LocalUser localUser,
                                                                             @PathVariable("username") String username) {
        if (Objects.equals(localUser.getUsername(), username))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        LocalUser user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(subscriptionService.getMutualFollowers(localUser, user));
    }
}
