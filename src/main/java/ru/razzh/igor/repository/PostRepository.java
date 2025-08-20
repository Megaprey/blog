package ru.razzh.igor.repository;

import ru.razzh.igor.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findFreeLastPosts();
    void save(Post post);

    List<Post> findByName(String name);

    Optional<Post> findById(Long id);

    void updatePost(Post post);
}
