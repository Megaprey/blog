package ru.razzh.igor.entity;


import java.time.LocalDateTime;

public class Comment {

    private Long id;

    private String text;

    private String author;

    private Long postId;

    private LocalDateTime createdAt;

    // Конструкторы
    public Comment() {}

    public Comment(String text, String author, Long postId) {
        this.text = text;
        this.author = author;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}