package com.rsmp.redditclone.service;

import com.rsmp.redditclone.auth.service.AuthService;
import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.mapper.PostMapper;
import com.rsmp.redditclone.model.dto.PostRequest;
import com.rsmp.redditclone.model.dto.PostResponse;
import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.Subreddit;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.repository.PostRepository;
import com.rsmp.redditclone.repository.SubredditRepository;
import com.rsmp.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;



    // Add new post
    @Transactional
    public void save(PostRequest postRequest) {
        log.info("Saving post {}", postRequest.getPostName());

        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringRedditException("Failed to find subreddit "
                        + postRequest.getSubredditName()));

        postRepository.save(postMapper
                .mapPostRequestToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

    // Get all posts
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        log.info("Retrieving all posts");

        return postRepository.findAll()
                .stream()
                .map(postMapper::mapPostToPostResponse)
                .collect(toList());
    }

    // Get post by id
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        log.info("Retrieving post with id {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Failed to find post with id " + id));

        return postMapper.mapPostToPostResponse(post);
    }

    // Get post by subreddit
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        log.info("Retrieving all posts with subreddit id {}", subredditId);

        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SpringRedditException("Failed to find subreddit with id " + subredditId));

        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapPostToPostResponse).collect(toList());
    }

    // Get post by username
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        log.info("Retrieving all posts created by user {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException(username));

        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapPostToPostResponse)
                .collect(toList());
    }
}

