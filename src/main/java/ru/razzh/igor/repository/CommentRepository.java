package ru.razzh.igor.repository;

import ru.razzh.igor.entity.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPost(Long postId);
    Comment save(Comment comment);
}
