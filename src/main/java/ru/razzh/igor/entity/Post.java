package ru.razzh.igor.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class Post {
    public Post(Long id, String name, String text, byte[] image, int likeCount, String tags) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.tags = tags;
        this.likeCount = likeCount;
        this.image = image;
    }

    private Long id;
    private String name;
    private String text;
    private String tags;
    private byte[] image;
    private int likeCount;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String text;
        private String tags;
        private byte[] image;
        private int likeCount;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder likeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Post build() {
            Post post = new Post();
            post.setId(this.id);
            post.setName(this.name);
            post.setText(this.text);
            post.setImage(this.image);
            post.setLikeCount(this.likeCount);
            post.setTags(this.tags);
            return post;
        }
    }}
