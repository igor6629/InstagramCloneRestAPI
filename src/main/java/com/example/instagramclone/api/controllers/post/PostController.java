package com.example.instagramclone.api.controllers.post;

import com.example.instagramclone.api.models.CommentBody;
import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.dao.PostDAO;
import com.example.instagramclone.models.Comment;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.services.CommentService;
import com.example.instagramclone.services.LikeService;
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
    private LikeService likeService;
    private CommentService commentService;
    private final PostDAO postDAO;

    public PostController(PostService postService, UserService userService, LikeService likeService, CommentService commentService, PostDAO postDAO) {
        this.postService = postService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
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
        Optional<Post> opPost = postDAO.findById(id);

        if (opPost.isPresent()) {
            Post post = opPost.get();
            likeService.makeLike(user, id, post);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlike")
    public ResponseEntity<HttpStatus> unlikePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id) {
        Optional<Post> opPost = postDAO.findById(id);

        if (opPost.isPresent()) {
            Post post = opPost.get();
            likeService.makeUnlike(user, id, post);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<HttpStatus> addComment(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id, @RequestBody CommentBody commentBody) {
        Optional<Post> opPost = postDAO.findById(id);

        if (opPost.isPresent()) {
            Post post = opPost.get();
            commentService.addComment(post, user, commentBody.getText());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/comment/{id_comment}")
    public ResponseEntity<HttpStatus> deleteComment(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id, @PathVariable("id_comment") Long idComment) {
        Optional<Post> opPost = postDAO.findById(id);

        if (opPost.isPresent()) {
            Post post = opPost.get();
            Comment comment = commentService.getCommentById(idComment);

            if (comment != null && comment.getPost() == post) {
                commentService.deleteComment(idComment, post);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }
}
