package ru.razzh.igor.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    private String name;
    private String text;
    private String tags;

    private int likeCount;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }


    public int getLikeCount() {
        return likeCount;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private PostDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.text = builder.text;
        this.tags = builder.tags;
        this.likeCount = builder.likeCount;
    }
    // Builder class
    public static class Builder {
        private Long id;
        private String name;
        private String text;
        private String tags;
        private int likeCount;

        public Builder() {}

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

        public Builder likeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public PostDto build() {
            return new PostDto(this);
        }
    }

    // Static factory method for builder
    public static Builder builder() {
        return new Builder();
    }

    // Optional: toString method
    @Override
    public String toString() {
        return "PostDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", tags='" + tags + '\'' +
                ", likeCount=" + likeCount +
                '}';
    }
}
