package ru.razzh.igor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.razzh.igor.entity.Post;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcNativePostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Post> findFreeLastPosts() {
        return jdbcTemplate.query(
                "select id, name, text, image, tags, likes_count from posts order by id desc limit 3",
                (rs, rowNum) -> new Post(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("text"),
                        rs.getBytes("image"),
                        rs.getInt("likes_count"),
                        rs.getString("tags")));
    }

    @Override
    public void save(Post post) {
        jdbcTemplate.update("insert into posts(name, text, image, likes_count, tags) values(?, ?, ?, ?, ?)",
                post.getName(), post.getText(), post.getImage(), post.getLikeCount(), post.getTags());
    }

    @Override
    public Optional<Post> findByName(String name) {
        String sql = "SELECT id, name, text, image, tags, likes_count FROM posts WHERE name = ?";

        try {
            Post post = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{name},
                    (rs, rowNum) -> {
                        Post p = new Post();
                        p.setId(rs.getLong("id"));
                        p.setName(rs.getString("name"));
                        p.setText(rs.getString("text"));
                        p.setImage(rs.getBytes("image"));
                        p.setTags(rs.getString("tags"));
                        p.setLikeCount(rs.getInt("likes_count"));
                        return p;
                    }
            );
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT id, name, text, image, tags, likes_count FROM posts WHERE id = ?";

        try {
            Post post = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{id},
                    (rs, rowNum) -> {
                        Post p = new Post();
                        p.setId(rs.getLong("id"));
                        p.setName(rs.getString("name"));
                        p.setText(rs.getString("text"));
                        p.setImage(rs.getBytes("image"));
                        p.setTags(rs.getString("tags"));
                        p.setLikeCount(rs.getInt("likes_count"));
                        return p;
                    }
            );
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updatePost(Post post) {
        String sql = "UPDATE posts SET name = ?, text = ?, tags = ?, likes_count = ?, image = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                post.getName(),
                post.getText(),
                post.getTags(),
                post.getLikeCount(),
                post.getImage(),
                post.getId());
    }


}
