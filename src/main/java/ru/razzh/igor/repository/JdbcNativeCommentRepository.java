package ru.razzh.igor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.razzh.igor.entity.Comment;

import java.util.List;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> findByPost(Long postId) {
        return jdbcTemplate.query("select id, text where post_id = ?", new Object[]{postId},
                (rs, rowNum) -> new Comment(rs.getLong("id"),
                    rs.getString("text"), postId));
    }

    @Override
    public void save(Comment comment) {
        jdbcTemplate.update("insert into comment(text, post_id) values(?, ?)");
    }
}
