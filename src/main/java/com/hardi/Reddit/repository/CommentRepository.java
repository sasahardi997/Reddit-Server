package com.hardi.Reddit.repository;

import com.hardi.Reddit.domain.Comment;
import com.hardi.Reddit.domain.Post;
import com.hardi.Reddit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
