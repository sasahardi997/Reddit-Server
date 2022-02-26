package com.hardi.Reddit.repository;

import com.hardi.Reddit.domain.Post;
import com.hardi.Reddit.domain.Subreddit;
import com.hardi.Reddit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBySubreddit(Subreddit subreddit);
    List<Post> findByUser(User user);
}
