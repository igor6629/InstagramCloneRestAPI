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

        Optional<LocalUser> opFollowingUser = userService.getUserByUsername(username);

        if (opFollowingUser.isPresent()) {
            LocalUser followingUser = opFollowingUser.get();
            subscriptionService.follow(user, followingUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/unfollow")
    public ResponseEntity<HttpStatus> unfollowUser(@AuthenticationPrincipal LocalUser user,
                                                   @PathVariable("username") String username) {

        if (Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Optional<LocalUser> opFollowingUser = userService.getUserByUsername(username);

        if (opFollowingUser.isPresent()) {
            LocalUser followingUser = opFollowingUser.get();
            subscriptionService.unfollow(user, followingUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<LocalUser> getUserProfile(@AuthenticationPrincipal LocalUser user,
                                                     @PathVariable("username") String username) {

        Optional<LocalUser> opUser = userService.getUserByUsername(username);
        return opUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<SubscriptionResponse>> getUserFollowing(@AuthenticationPrincipal LocalUser user,
                                                                       @PathVariable("username") String username) {

        Optional<LocalUser> opFollowingUser = userService.getUserByUsername(username);

        if (opFollowingUser.isPresent()) {
            return ResponseEntity.ok(subscriptionService.getFollowings(opFollowingUser.get()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<SubscriptionResponse>> getUserFollowers(@AuthenticationPrincipal LocalUser user,
                                                                       @PathVariable("username") String username) {

        Optional<LocalUser> opUser = userService.getUserByUsername(username);

        if (opUser.isPresent()) {
            return ResponseEntity.ok(subscriptionService.getFollowers(opUser.get()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{username}/mutual")
    public ResponseEntity<List<SubscriptionResponse>> getUserMutualFollowers(@AuthenticationPrincipal LocalUser user,
                                                                             @PathVariable("username") String username) {

        if (Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Optional<LocalUser> opUser = userService.getUserByUsername(username);

        if (opUser.isPresent()) {
            return ResponseEntity.ok(subscriptionService.getMutualFollowers(user, opUser.get()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
