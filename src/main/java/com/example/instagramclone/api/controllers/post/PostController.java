package com.example.instagramclone.api.controllers.post;

import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/add")
    public ResponseEntity addPost(@AuthenticationPrincipal LocalUser user, @Valid @RequestBody PostBody postBody, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
        {
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        postService.createPost(postBody, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/show")
    public List<Post> getPosts(@AuthenticationPrincipal LocalUser user) {
        return postService.getAllPosts(user);
    }
}
