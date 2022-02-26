package com.hardi.Reddit.controller;

import com.hardi.Reddit.domain.dto.CommentDTO;
import com.hardi.Reddit.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDTO commentDTO) {
        commentService.save(commentDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/by-post/{id}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsForPost(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(id));
    }

    @GetMapping("/by-user/{id}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsForUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(id));
    }
}
