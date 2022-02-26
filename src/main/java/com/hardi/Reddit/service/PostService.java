package com.hardi.Reddit.service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.hardi.Reddit.domain.*;
import com.hardi.Reddit.domain.dto.PostRequestDTO;
import com.hardi.Reddit.domain.dto.PostResponseDTO;
import com.hardi.Reddit.exceptions.PostNotFoundException;
import com.hardi.Reddit.exceptions.SubredditNotFoundException;
import com.hardi.Reddit.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hardi.Reddit.domain.VoteType.DOWNVOTE;
import static com.hardi.Reddit.domain.VoteType.UPVOTE;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;

    public void save(PostRequestDTO postRequestDTO) {
        try {
            Post post = convertToPost(postRequestDTO);

            Subreddit subreddit = subredditRepository.findById(postRequestDTO.getSubredditId()).get();
            post.setSubreddit(subreddit);
            post.setUser(authService.getCurrentUser());

            postRepository.save(post);
        } catch (Exception e) {
            throw new SubredditNotFoundException("Didn't found Subreddit with ID - " + postRequestDTO.getSubredditId());
        }
    }

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID - " + id + " not found"));
        return convertToResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException("Didn't found Subreddit with ID - " + id));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostsByUsername(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + name + " not found"));
        List<Post> posts = postRepository.findByUser(user);
        return posts
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }


    private Post convertToPost(PostRequestDTO postRequestDTO) {
        return Post.builder()
                .postId(postRequestDTO.getPostId())
                .postName(postRequestDTO.getPostName())
                .description(postRequestDTO.getDescription())
                .url(postRequestDTO.getUrl())
                .createdDate(Instant.now())
                .voteCount(0)
                .build();
    }

    private PostResponseDTO convertToResponse(Post post) {
        return PostResponseDTO.builder()
                .id(post.getPostId())
                .postName(post.getPostName())
                .url(post.getUrl())
                .description(post.getDescription())
                .userName(post.getUser() == null ? null : post.getUser().getUsername())
                .subredditName(post.getSubreddit().getName())
                .voteCount(post.getVoteCount())
                .commentCount(commentCount(post))
                .duration(getDuration(post))
                .upVote(isPostUpVoted(post))
                .downVote(isPostDownVoted(post))
                .build();
    }
}
