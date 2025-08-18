package ru.razzh.igor.dto;

import org.springframework.web.multipart.MultipartFile;

public class PostDto {
    private String name;
    private String text;
    private String tags;
    private MultipartFile image;
    private int likeCount;

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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

//    public PostDto(String name, String text, MultipartFile image) {
//        this.name = name;
//        this.text = text;
//        this.image = image;
//    }
}
