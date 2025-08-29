package ru.razzh.igor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class PostExtendedInDto extends PostDto {
    public PostExtendedInDto(Long id, String name, String text, String tags, int likeCount, MultipartFile image) {
        super(id, name, text, tags, likeCount);
        this.image = image;
    }

    private MultipartFile image;
}
