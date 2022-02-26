package com.hardi.Reddit.service;

import com.hardi.Reddit.domain.Comment;
import com.hardi.Reddit.domain.NotificationEmail;
import com.hardi.Reddit.domain.Post;
import com.hardi.Reddit.domain.User;
import com.hardi.Reddit.domain.dto.CommentDTO;
import com.hardi.Reddit.exceptions.PostNotFoundException;
import com.hardi.Reddit.repository.CommentRepository;
import com.hardi.Reddit.repository.PostRepository;
import com.hardi.Reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private static final String POST_URL = "";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;

    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post with ID - " + commentDTO.getPostId() + " not found."));
        User user = authService.getCurrentUser() == null ? null : authService.getCurrentUser();
        Comment comment = convertToComment(commentDTO);
        comment.setUser(user);
        comment.setPost(post);

        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
    }

    public List<CommentDTO> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post with ID - " + postId + " not found."));
         return commentRepository.findByPost(post)
                 .stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
    }

    public List<CommentDTO> getAllCommentsForUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with ID - " + id + " not found"));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    private CommentDTO convertToDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getId())
                .userId(comment.getUser() == null ? null : comment.getUser().getUserId())
                .userName(comment.getUser() == null ? null : comment.getUser().getUsername())
                .createdDate(comment.getCreatedDate())
                .text(comment.getText())
                .build();
    }

    private Comment convertToComment(CommentDTO commentDTO) {
        return Comment.builder()
                .text(commentDTO.getText())
                .createdDate(Instant.now())
                .id(commentDTO.getId() == null ? null : commentDTO.getId())
                .build();
    }

}
