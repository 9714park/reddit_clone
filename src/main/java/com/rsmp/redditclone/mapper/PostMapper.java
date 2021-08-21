package com.rsmp.redditclone.mapper;

import com.rsmp.redditclone.model.dto.PostRequest;
import com.rsmp.redditclone.model.dto.PostResponse;
import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.Subreddit;
import com.rsmp.redditclone.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "id", source = "postRequest.id")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "user")
    @Mapping(target="createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source="postRequest.description")
    Post mapPostRequestToPost(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "subredditName", source="subreddit.name")
    @Mapping(target = "username", source="user.username")
    PostResponse mapPostToPostResponse(Post post);

}
