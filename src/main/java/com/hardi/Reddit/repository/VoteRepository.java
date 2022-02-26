package com.hardi.Reddit.repository;

import com.hardi.Reddit.domain.Post;
import com.hardi.Reddit.domain.User;
import com.hardi.Reddit.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
