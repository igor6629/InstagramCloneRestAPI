package com.example.instagramclone.api.controllers.main;

import com.example.instagramclone.api.models.PostResponse;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.services.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {

    private PostService postService;

    public MainController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public List<PostResponse> getLastPostsOfFollowingUsers(@AuthenticationPrincipal LocalUser user) {
        return postService.findLastPostsOfFollowingUsers(user);
    }
}
