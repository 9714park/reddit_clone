package com.rsmp.redditclone.repository;

import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.Subreddit;
import com.rsmp.redditclone.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
