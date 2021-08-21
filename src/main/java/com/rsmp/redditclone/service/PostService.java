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


    @Transactional
    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringRedditException("Failed to find subreddit "
                        + postRequest.getSubredditName()));
        postRepository.save(postMapper
                .mapPostRequestToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapPostToPostResponse)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Failed to find post with id "
                        + id.toString()));
        return postMapper.mapPostToPostResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SpringRedditException("Failed to find subreddit with id "
                        + subredditId.toString()));

        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapPostToPostResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException(username));

        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapPostToPostResponse)
                .collect(toList());
    }
}

