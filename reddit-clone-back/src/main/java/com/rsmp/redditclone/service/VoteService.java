package com.rsmp.redditclone.service;

import com.rsmp.redditclone.auth.service.AuthService;
import com.rsmp.redditclone.exception.SpringRedditException;
import com.rsmp.redditclone.model.dto.VoteDto;
import com.rsmp.redditclone.model.entity.Post;
import com.rsmp.redditclone.model.entity.Vote;
import com.rsmp.redditclone.repository.PostRepository;
import com.rsmp.redditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.rsmp.redditclone.model.enums.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        // Find post
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new SpringRedditException("Failed to find post with id " + voteDto
                        .getPostId()));

        // Find previous user vote on the post
        Optional<Vote> voteByPostAndUser = voteRepository
                .findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            return;
        }

        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}