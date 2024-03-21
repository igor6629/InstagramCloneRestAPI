package com.example.instagramclone.api.controllers.post;

import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.services.PostService;
import com.example.instagramclone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/post")
public class PostController {

    private PostService postService;
    private UserService userService;
    private final PostDAO postDAO;

    public PostController(PostService postService, UserService userService, PostDAO postDAO) {
        this.postService = postService;
        this.userService = userService;
        this.postDAO = postDAO;
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

    @GetMapping("/{username}/show")
    public ResponseEntity<List<Post>> getPosts(@AuthenticationPrincipal LocalUser localUser, @PathVariable("username") String username) {
        Optional<LocalUser> opUser = userService.getUserByUsername(username);

        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            return ResponseEntity.ok(postService.getAllPosts(user));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") long id) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{username}/show/{id}")
    public ResponseEntity<HttpStatus> deletePostById(@AuthenticationPrincipal LocalUser user,
                                                     @PathVariable("username") String username, @PathVariable("id") Long id) {

        if (!Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        postService.deletePostById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/show/{id}/update")
    public ResponseEntity<HttpStatus> updatePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("username") String username,
                                                     @PathVariable("id") Long id, @RequestBody PostBody postBody) {

        if (!Objects.equals(user.getUsername(), username))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (postBody.getLocation() != null)
            post.setLocation(postBody.getLocation());

        if (postBody.getCaption() != null)
            post.setCaption(postBody.getCaption());

        postDAO.save(post);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<HttpStatus> likePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id) {

        return ResponseEntity.ok().build();
    }
}
