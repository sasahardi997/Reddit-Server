package com.hardi.Reddit.service;

import com.hardi.Reddit.domain.Subreddit;
import com.hardi.Reddit.domain.dto.SubredditDTO;
import com.hardi.Reddit.exceptions.SpringRedditException;
import com.hardi.Reddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;

    @Transactional
    public SubredditDTO save(SubredditDTO subredditDTO) {
        Subreddit saved = subredditRepository.save(convertToSubreddit(subredditDTO));
        subredditDTO.setId(saved.getId());
        return subredditDTO;
    }

    @Transactional(readOnly = true)
    public List<SubredditDTO> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::convertToSubredditDTO)
                .collect(toList());
    }

    public SubredditDTO getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return convertToSubredditDTO(subreddit);
    }

    private SubredditDTO convertToSubredditDTO(Subreddit subreddit) {
        return SubredditDTO.builder()
                .id(subreddit.getId())
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }

    private Subreddit convertToSubreddit(SubredditDTO subredditDTO) {
        return Subreddit.builder()
                .name(subredditDTO.getName())
                .description(subredditDTO.getDescription())
                .build();
    }


}
