package com.rsmp.redditclone.service;


import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.mapper.SubredditMapper;
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
    private final SubredditMapper subredditMapper;

    // Add new subreddit
    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        log.info("Saving subreddit {}", subredditDto.getName()
        );
        Subreddit subreddit = subredditMapper.mapDtoToSubreddit(subredditDto);

        Subreddit save = subredditRepository.save(subreddit);
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    // Get all subreddits
    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        log.info("Retrieving all subreddits");

        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(Collectors.toList());
    }

    // Get subreddit by id
    public SubredditDto getSubreddit(Long id) {
        log.info("Retrieving subreddit by id {}", id);
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException(
                        "Failed to find subreddit with id " + id));

        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
