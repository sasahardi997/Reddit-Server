package com.hardi.Reddit.service;

import com.hardi.Reddit.domain.Post;
import com.hardi.Reddit.domain.Vote;
import com.hardi.Reddit.domain.dto.VoteDTO;
import com.hardi.Reddit.exceptions.PostNotFoundException;
import com.hardi.Reddit.exceptions.SpringRedditException;
import com.hardi.Reddit.repository.PostRepository;
import com.hardi.Reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.hardi.Reddit.domain.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDTO voteDTO){
        Post post = postRepository.findById(voteDTO.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post with ID - " + voteDTO.getPostId() + " not found"));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() &&
            voteByPostAndUser.get().getVoteType().equals(voteDTO.getVoteType())){
            throw new SpringRedditException("You have already "
                    + voteDTO.getVoteType() + "'d for this post");
        }

        if(UPVOTE.equals(voteDTO.getVoteType())){
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(convertToVote(voteDTO, post));
        postRepository.save(post);
    }

    private Vote convertToVote(VoteDTO voteDTO, Post post) {
        return Vote.builder()
                .voteType(voteDTO.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
