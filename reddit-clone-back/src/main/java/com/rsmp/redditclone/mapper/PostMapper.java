package com.rsmp.redditclone.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.rsmp.redditclone.auth.service.AuthService;
import com.rsmp.redditclone.model.dto.PostRequest;
import com.rsmp.redditclone.model.dto.PostResponse;
import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.Subreddit;
import com.rsmp.redditclone.model.entity.User;
import com.rsmp.redditclone.repository.CommentRepository;
import com.rsmp.redditclone.repository.VoteRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "id", source = "postRequest.id")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "user")
    @Mapping(target="createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source="postRequest.description")
    @Mapping(target = "voteCount", constant = "0")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "subredditName", source="subreddit.name")
    @Mapping(target = "username", source="user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToPostResponse(Post post);

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreateDate().toEpochMilli());
    }

}
