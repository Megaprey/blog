package ru.razzh.igor.dto;

public record CommentResponse(Long id, String author, String text, Long postId) {
}
