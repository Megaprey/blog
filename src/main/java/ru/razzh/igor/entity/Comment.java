package ru.razzh.igor.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    public Comment(Long id, String text, Long post_id) {
        this.id = id;
        this.text = text;
        this.post_id = post_id;
    }

    private Long id;
    private String text;
    private Long post_id;
}
