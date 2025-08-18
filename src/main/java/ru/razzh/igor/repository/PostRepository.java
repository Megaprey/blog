package ru.razzh.igor.repository;

import ru.razzh.igor.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll();
    void save(Post post);

    Optional<Post> findByName(String name);
}
