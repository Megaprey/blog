package ru.razzh.igor.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.razzh.igor.entity.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(JdbcNativeCommentRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.sql.init.mode=always"
})
class JdbcNativeCommentRepositoryTest {

    @Autowired
    private JdbcNativeCommentRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем таблицы перед каждым тестом
        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void findByPost_ShouldReturnComments_WhenCommentsExistForPost() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "User1", "First comment", postId
        );
        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "User2", "Second comment", postId
        );

        // When
        List<Comment> comments = repository.findByPost(postId);

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments)
                .allMatch(comment -> comment.getPostId().equals(postId))
                .extracting(Comment::getAuthor)
                .containsExactlyInAnyOrder("User1", "User2");
    }

    @Test
    void findByPost_ShouldReturnEmptyList_WhenNoCommentsForPost() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        // When
        List<Comment> comments = repository.findByPost(postId);

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    void findByPost_ShouldReturnEmptyList_WhenPostNotExists() {
        // When
        List<Comment> comments = repository.findByPost(999L);

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    void save_ShouldInsertNewComment_WhenCommentIdIsNull() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        Comment newComment = new Comment();
        newComment.setAuthor("New User");
        newComment.setText("New comment text");
        newComment.setPostId(postId);
        // id is null - это новая запись

        // When
        Comment savedComment = repository.save(newComment);

        // Then
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getAuthor()).isEqualTo("New User");
        assertThat(savedComment.getText()).isEqualTo("New comment text");
        assertThat(savedComment.getPostId()).isEqualTo(postId);

        // Проверяем что комментарий действительно сохранен в БД
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE author = ? AND text = ?",
                Integer.class, "New User", "New comment text"
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void save_ShouldUpdateExistingComment_WhenCommentIdIsNotNull() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "Old Author", "Old Text", postId
        );
        Long commentId = jdbcTemplate.queryForObject(
                "SELECT id FROM comment WHERE author = ?", Long.class, "Old Author"
        );

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setAuthor("Updated Author");
        existingComment.setText("Updated Text");
        existingComment.setPostId(postId);

        // When
        Comment updatedComment = repository.save(existingComment);

        // Then
        assertThat(updatedComment.getId()).isEqualTo(commentId);
        assertThat(updatedComment.getAuthor()).isEqualTo("Updated Author");
        assertThat(updatedComment.getText()).isEqualTo("Updated Text");
        assertThat(updatedComment.getPostId()).isEqualTo(postId);

        // Проверяем что комментарий обновлен в БД
        String author = jdbcTemplate.queryForObject(
                "SELECT author FROM comment WHERE id = ?", String.class, commentId
        );
        assertThat(author).isEqualTo("Updated Author");
    }

    @Test
    void save_ShouldUpdateCommentWithDifferentPostId() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Original Post", "Original Content"
        );
        Long originalPostId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Original Post"
        );

        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "New Post", "New Content"
        );
        Long newPostId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "New Post"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "Author", "Text", originalPostId
        );
        Long commentId = jdbcTemplate.queryForObject(
                "SELECT id FROM comment WHERE author = ?", Long.class, "Author"
        );

        Comment commentToUpdate = new Comment();
        commentToUpdate.setId(commentId);
        commentToUpdate.setAuthor("Author");
        commentToUpdate.setText("Text");
        commentToUpdate.setPostId(newPostId); // Меняем post_id

        // When
        Comment updatedComment = repository.save(commentToUpdate);

        // Then
        assertThat(updatedComment.getPostId()).isEqualTo(newPostId);

        // Проверяем что post_id изменился в БД
        Long actualPostId = jdbcTemplate.queryForObject(
                "SELECT post_id FROM comment WHERE id = ?", Long.class, commentId
        );
        assertThat(actualPostId).isEqualTo(newPostId);
    }

    @Test
    void findByPost_ShouldReturnCommentsForSpecificPostOnly() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Post 1", "Content 1"
        );
        Long postId1 = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Post 1"
        );

        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Post 2", "Content 2"
        );
        Long postId2 = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Post 2"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "User1", "Comment for post 1", postId1
        );
        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "User2", "Comment for post 2", postId2
        );

        // When
        List<Comment> commentsForPost1 = repository.findByPost(postId1);
        List<Comment> commentsForPost2 = repository.findByPost(postId2);

        // Then
        assertThat(commentsForPost1).hasSize(1);
        assertThat(commentsForPost1.get(0).getAuthor()).isEqualTo("User1");
        assertThat(commentsForPost1.get(0).getText()).isEqualTo("Comment for post 1");

        assertThat(commentsForPost2).hasSize(1);
        assertThat(commentsForPost2.get(0).getAuthor()).isEqualTo("User2");
        assertThat(commentsForPost2.get(0).getText()).isEqualTo("Comment for post 2");
    }

    @Test
    void save_ShouldNotAffectOtherComments() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "Author1", "Text1", postId
        );
        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "Author2", "Text2", postId
        );

        Long commentId1 = jdbcTemplate.queryForObject(
                "SELECT id FROM comment WHERE author = ?", Long.class, "Author1"
        );

        // Обновляем только первый комментарий
        Comment commentToUpdate = new Comment();
        commentToUpdate.setId(commentId1);
        commentToUpdate.setAuthor("Updated Author1");
        commentToUpdate.setText("Updated Text1");
        commentToUpdate.setPostId(postId);

        // When
        repository.save(commentToUpdate);

        // Then - проверяем что второй комментарий не изменился
        String author2 = jdbcTemplate.queryForObject(
                "SELECT author FROM comment WHERE author = ?", String.class, "Author2"
        );
        assertThat(author2).isEqualTo("Author2");

        String text2 = jdbcTemplate.queryForObject(
                "SELECT text FROM comment WHERE author = ?", String.class, "Author2"
        );
        assertThat(text2).isEqualTo("Text2");
    }

    @Test
    void findByPost_ShouldReturnCommentsInCorrectStructure() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text) VALUES (?, ?)",
                "Test Post", "Test Content"
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        jdbcTemplate.update(
                "INSERT INTO comment (author, text, post_id) VALUES (?, ?, ?)",
                "John", "Great post!", postId
        );

        // When
        List<Comment> comments = repository.findByPost(postId);

        // Then - проверяем структуру возвращаемых объектов
        assertThat(comments).hasSize(1);
        Comment comment = comments.get(0);
        assertThat(comment.getAuthor()).isEqualTo("John");
        assertThat(comment.getText()).isEqualTo("Great post!");
        assertThat(comment.getPostId()).isEqualTo(postId);
        // Метод findByPost не устанавливает id, только author, text и postId
    }
}
