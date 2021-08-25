package com.rsmp.redditclone.service;

import com.rsmp.redditclone.auth.service.AuthService;
import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.mapper.CommentMapper;
import com.rsmp.redditclone.model.NotificationEmail;
import com.rsmp.redditclone.model.dto.CommentsDto;
import com.rsmp.redditclone.model.entity.Comment;
import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.repository.CommentRepository;
import com.rsmp.redditclone.repository.PostRepository;
import com.rsmp.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailBuilder mailBuilder;
    private final MailService mailService;

    public void createComment(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new SpringRedditException("Failed to find post with id "
                        + commentsDto.getPostId()));

        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailBuilder
                .build(post.getUser().getUsername() + " posted a comment on your post." + post.getUrl());
        sendCommentNotification(message, post.getUser());
    }

    public List<CommentsDto> getCommentByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SpringRedditException("Failed to find post with id " + postId));

        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }

    public List<CommentsDto> getCommentsByUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }

    private void sendCommentNotification(String message, User user) {
        mailService
                .sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user
                        .getEmail(), message));
    }
}