package ru.razzh.igor.repository;

import ru.razzh.igor.entity.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPost(Long postId);
    void save(Comment comment);
}
