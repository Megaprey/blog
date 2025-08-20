package ru.razzh.igor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.map.Mapper;
import ru.razzh.igor.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void save(PostDto postDto) {
        try {
            postRepository.save(Mapper.postDtoMapToPost(postDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> findFreeLastPosts() {
        return postRepository.findFreeLastPosts();
    }

    public List<Post> findByName(String name) {
        return postRepository.findByName(name);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void updatePost(PostDto postDto) throws SQLException, IOException {
        postRepository.updatePost(Mapper.postDtoMapToPost(postDto));
    }
}
