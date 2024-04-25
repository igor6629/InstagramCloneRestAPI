package com.example.instagramclone.api.controllers.post;

import com.example.instagramclone.api.models.CommentBody;
import com.example.instagramclone.api.models.PostBody;
import com.example.instagramclone.api.models.PostResponse;
import com.example.instagramclone.api.models.UpdatePostBody;
import com.example.instagramclone.models.Comment;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.services.CommentService;
import com.example.instagramclone.services.LikeService;
import com.example.instagramclone.services.PostService;
import com.example.instagramclone.services.UserService;
import com.example.instagramclone.util.Util;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final LikeService likeService;
    private final CommentService commentService;

    public PostController(PostService postService, UserService userService, LikeService likeService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addPost(@AuthenticationPrincipal LocalUser user,
                                                       @Valid @RequestBody PostBody postBody, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return Util.getErrors(bindingResult);

        postService.createPost(postBody, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/show")
    public ResponseEntity<List<PostResponse>> getPosts(@AuthenticationPrincipal LocalUser localUser,
                                                       @PathVariable("username") String username) {
        LocalUser user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(postService.getAllPosts(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") long id) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!Objects.equals(post.getUser().getUsername(), user.getUsername()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        postService.deletePostById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<HttpStatus> updatePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id,
                                                     @RequestBody UpdatePostBody updatePostBody) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!Objects.equals(post.getUser().getUsername(), user.getUsername()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        postService.updatePost(post, updatePostBody);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<HttpStatus> likePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        likeService.makeLike(user, post);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlike")
    public ResponseEntity<HttpStatus> unlikePostById(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id) {
        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        likeService.makeUnlike(user, id, post);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<Map<String, String>> addComment(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id,
                                                 @Valid @RequestBody CommentBody commentBody, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return Util.getErrors(bindingResult);

        Post post = postService.getPostById(id);

        if (post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        commentService.addComment(post, user, commentBody.getComment());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/comment/{id_comment}")
    public ResponseEntity<HttpStatus> deleteComment(@AuthenticationPrincipal LocalUser user, @PathVariable("id") Long id,
                                                    @PathVariable("id_comment") Long idComment) {
        Post post = postService.getPostById(id);
        Comment comment = commentService.getCommentById(idComment);

        if (post != null && comment != null && comment.getPost() == post) {
            if (!Objects.equals(post.getUser().getUsername(), user.getUsername())
                    && !Objects.equals(comment.getLocalUser().getUsername(), user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            commentService.deleteComment(idComment, post);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }
}
