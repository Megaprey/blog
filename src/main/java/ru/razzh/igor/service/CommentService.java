package ru.razzh.igor.service;

import org.springframework.stereotype.Service;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public void save(Comment comment) {
        commentRepository.save(comment);
    }
    // Получение комментариев
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPost(postId);
    }
}
