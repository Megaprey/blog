package ru.razzh.igor.map;

import org.springframework.web.multipart.MultipartFile;
import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.dto.PostExtendedInDto;
import ru.razzh.igor.dto.PostExtendedOutDto;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;

public class Mapper {
    public static Post postDtoMapToPost(PostExtendedInDto postDto) throws IOException, SQLException {
        return Post.builder()
                .id(postDto.getId())
                .name(postDto.getName())
                .text(postDto.getText())
                .image(Utils.multipartFileToMassByte(postDto.getImage()))
                .tags(postDto.getTags())
                .likeCount(postDto.getLikeCount())
                .build();
    }
    public static PostDto postMapToPostDto(Post post) throws IOException, SQLException {
        return new PostDto(post.getId(), post.getName(), post.getText(),
                post.getTags(), post.getLikeCount());
    }
}
