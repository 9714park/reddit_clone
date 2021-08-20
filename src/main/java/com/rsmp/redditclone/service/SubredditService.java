package com.rsmp.redditclone.service;


import com.rsmp.redditclone.model.dto.SubredditDto;
import com.rsmp.redditclone.model.entity.Subreddit;
import com.rsmp.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    private final SubredditRepository subredditRepository;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = mapSubredditDto(subredditDto);

        Subreddit save = subredditRepository.save(subreddit);
        subredditDto.setId(save.getId());
        return subredditDto;
    }


    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToSubRedditDto)
                .collect(Collectors.toList());
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription()).build();
    }

    private SubredditDto mapToSubRedditDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .description(subreddit.getDescription())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }
}
