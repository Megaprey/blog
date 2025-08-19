package ru.razzh.igor.map;

import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.util.Utils;

import java.io.IOException;
import java.sql.SQLException;

public class Mapper {
    public static Post postDtoMapToPost(PostDto postDto) throws IOException, SQLException {
        return Post.builder()
                .id(postDto.getId())
                .name(postDto.getName())
                .text(postDto.getText())
                .image(Utils.multipartFileToMassByte(postDto.getImage()))
                .tags(postDto.getTags())
                .likeCount(postDto.getLikeCount())
                .build();
    }
}
