package ru.razzh.igor.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    public Comment(String text, Long post_id) {
        this.text = text;
        this.postId = post_id;
    }

    public Comment(Long id, String text, Long post_id) {
        this.id = id;
        this.text = text;
        this.postId = post_id;
    }

    private Long id;
    private String text;
    private Long postId;
}
