package com.rsmp.redditclone.controller;

import com.rsmp.redditclone.model.dto.CommentsDto;
import com.rsmp.redditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentsController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
        commentService.createComment(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/post")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@RequestParam("id") Long postId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getCommentByPost(postId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@RequestParam("username") String username) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getCommentsByUser(username));
    }
}
