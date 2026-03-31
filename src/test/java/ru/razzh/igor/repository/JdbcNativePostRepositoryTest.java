package ru.razzh.igor.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.razzh.igor.entity.Post;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(JdbcNativePostRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.sql.init.mode=always"
})
class JdbcNativePostRepositoryTest {

    @Autowired
    private JdbcNativePostRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void save_ShouldSavePostWithAllFields() {
        // Given
        byte[] imageBytes = "test-image".getBytes();
        Post post = new Post();
        post.setName("Test Post");
        post.setText("Test Content");
        post.setImage(imageBytes);
        post.setTags("java,spring");
        post.setLikeCount(10);

        // When
        repository.save(post);

        // Then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE name = ?",
                Integer.class, "Test Post"
        );
        assertThat(count).isEqualTo(1);

        // Проверяем что все поля сохранились корректно
        Post savedPost = jdbcTemplate.queryForObject(
                "SELECT name, text, image, tags, likes_count FROM posts WHERE name = ?",
                (rs, rowNum) -> {
                    Post p = new Post();
                    p.setName(rs.getString("name"));
                    p.setText(rs.getString("text"));
                    p.setImage(rs.getBytes("image"));
                    p.setTags(rs.getString("tags"));
                    p.setLikeCount(rs.getInt("likes_count"));
                    return p;
                },
                "Test Post"
        );

        assertThat(savedPost.getName()).isEqualTo("Test Post");
        assertThat(savedPost.getText()).isEqualTo("Test Content");
        assertThat(savedPost.getImage()).isEqualTo(imageBytes);
        assertThat(savedPost.getTags()).isEqualTo("java,spring");
        assertThat(savedPost.getLikeCount()).isEqualTo(10);
    }

    @Test
    void save_ShouldSavePostWithNullImage() {
        // Given
        Post post = new Post();
        post.setName("Test Post Without Image");
        post.setText("Test Content");
        post.setImage(null);
        post.setTags("test");
        post.setLikeCount(0);

        // When
        repository.save(post);

        // Then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE name = ?",
                Integer.class, "Test Post Without Image"
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void findById_ShouldReturnPost_WhenPostExists() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                "Test Post", "Test Content", "java,spring", 5
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Test Post"
        );

        // When
        Optional<Post> foundPost = repository.findById(postId);

        // Then
        assertThat(foundPost).isPresent();
        Post post = foundPost.get();
        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getName()).isEqualTo("Test Post");
        assertThat(post.getText()).isEqualTo("Test Content");
        assertThat(post.getTags()).isEqualTo("java,spring");
        assertThat(post.getLikeCount()).isEqualTo(5);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenPostNotExists() {
        // When
        Optional<Post> foundPost = repository.findById(999L);

        // Then
        assertThat(foundPost).isEmpty();
    }

    @Test
    void findByName_ShouldReturnPosts_WhenPostsWithSameNameExist() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Common Name", "Content 1", "tag1"
        );
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Common Name", "Content 2", "tag2"
        );
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Different Name", "Content 3", "tag3"
        );

        // When
        List<Post> posts = repository.findByName("Common Name");

        // Then
        assertThat(posts).hasSize(2);
        assertThat(posts)
                .allMatch(post -> "Common Name".equals(post.getName()))
                .extracting(Post::getText)
                .containsExactlyInAnyOrder("Content 1", "Content 2");
    }

    @Test
    void findByName_ShouldReturnEmptyList_WhenNoPostsWithGivenName() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Existing Post", "Content", "tag"
        );

        // When
        List<Post> posts = repository.findByName("Non Existing Name");

        // Then
        assertThat(posts).isEmpty();
    }

    @Test
    void findFreeLastPosts_ShouldReturnLastThreePostsOrderedByIdDesc() {
        // Given
        for (int i = 1; i <= 5; i++) {
            jdbcTemplate.update(
                    "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                    "Post " + i, "Content " + i, "tag" + i
            );
        }

        // When
        List<Post> posts = repository.findFreeLastPosts();

        // Then
        assertThat(posts).hasSize(3);
        // Проверяем что возвращаются последние 3 поста (с наибольшими id) в порядке убывания id
        assertThat(posts)
                .extracting(Post::getName)
                .containsExactly("Post 5", "Post 4", "Post 3");

        assertThat(posts)
                .extracting(Post::getId)
                .isSortedAccordingTo((id1, id2) -> id2.compareTo(id1)); // descending order
    }

    @Test
    void findFreeLastPosts_ShouldReturnAllPosts_WhenLessThanThreePostsExist() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Post 1", "Content 1", "tag1"
        );
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                "Post 2", "Content 2", "tag2"
        );

        // When
        List<Post> posts = repository.findFreeLastPosts();

        // Then
        assertThat(posts).hasSize(2);
        assertThat(posts)
                .extracting(Post::getName)
                .containsExactly("Post 2", "Post 1");
    }

    @Test
    void findFreeLastPosts_ShouldReturnEmptyList_WhenNoPostsExist() {
        // When
        List<Post> posts = repository.findFreeLastPosts();

        // Then
        assertThat(posts).isEmpty();
    }

    @Test
    void updatePost_ShouldUpdateAllFieldsOfExistingPost() {
        // Given
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                "Old Name", "Old Content", "oldTag", 1
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Old Name"
        );

        System.out.println("postId = " + postId);
        byte[] newImage = "new-image".getBytes();
        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setName("New Name");
        updatedPost.setText("New Content");
        updatedPost.setTags("newTag");
        updatedPost.setLikeCount(10);
        updatedPost.setImage(newImage);

        // When
        repository.updatePost(updatedPost);

        // Then
        Post savedPost = jdbcTemplate.queryForObject(
                "SELECT name, text, tags, likes_count, image FROM posts WHERE id = ?",
                (rs, rowNum) -> {
                    Post p = new Post();
                    p.setName(rs.getString("name"));
                    p.setText(rs.getString("text"));
                    p.setTags(rs.getString("tags"));
                    p.setLikeCount(rs.getInt("likes_count"));
                    p.setImage(rs.getBytes("image"));
                    return p;
                },
                postId
        );

        assertThat(savedPost.getName()).isEqualTo("New Name");
        assertThat(savedPost.getText()).isEqualTo("New Content");
        assertThat(savedPost.getTags()).isEqualTo("newTag");
        assertThat(savedPost.getLikeCount()).isEqualTo(10);
        assertThat(savedPost.getImage()).isEqualTo(newImage);
    }

    @Test
    void updatePost_ShouldUpdatePostWithNullImage() {
        // Given
        byte[] initialImage = "initial-image".getBytes();
        jdbcTemplate.update(
                "INSERT INTO posts (name, text, tags, likes_count, image) VALUES (?, ?, ?, ?, ?)",
                "Post with Image", "Content", "tag", 5, initialImage
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE name = ?", Long.class, "Post with Image"
        );

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setName("Updated Post");
        updatedPost.setText("Updated Content");
        updatedPost.setTags("updatedTag");
        updatedPost.setLikeCount(15);
        updatedPost.setImage(null); // Устанавливаем image в null

        // When
        repository.updatePost(updatedPost);

        // Then
        byte[] resultImage = jdbcTemplate.queryForObject(
                "SELECT image FROM posts WHERE id = ?", byte[].class, postId
        );
        assertThat(resultImage).isNull();
    }
}