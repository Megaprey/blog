package ru.razzh.igor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.razzh.igor.entity.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            // INSERT нового комментария
            return insert(comment);
        } else {
            // UPDATE существующего комментария
            return update(comment);
        }
    }

    private Comment insert(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comment (text, post_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, keyHolder);

        // Устанавливаем сгенерированный ID
        comment.setId(keyHolder.getKey().longValue());

        return comment;
    }

    private Comment update(Comment comment) {
        jdbcTemplate.update(
                "UPDATE comment SET post_id = ?, text = ? WHERE id = ?",
                comment.getPostId(),
                comment.getText(),
                comment.getId()
        );

        return comment;
    }
}
