package ru.razzh.igor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PostExtendedOutDto extends PostDto {
    public PostExtendedOutDto(Long id, String name, String text, String tags, int likeCount, String image) {
        super(id, name, text, tags, likeCount);
        this.image = image;
    }

    private String image;
}
