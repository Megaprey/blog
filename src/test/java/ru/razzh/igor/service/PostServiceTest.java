package ru.razzh.igor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.razzh.igor.dto.CommentRequest;
import ru.razzh.igor.dto.PostExtendedInDto;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.repository.CommentRepository;
import ru.razzh.igor.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void save_ShouldSavePost_WhenValidPostDto() {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setName("Test Post");
        postDto.setText("Test Content");

        Post post = new Post();
        post.setName("Test Post");
        post.setText("Test Content");

        // When
        postService.save(postDto);

        // Then
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void save_ShouldThrowRuntimeException_WhenIOExceptionOccurs() {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setName("Test Post");
        
        // Используем RuntimeException вместо проверяемых исключений
        doThrow(new RuntimeException("File error", new IOException("File error")))
            .when(postRepository).save(any(Post.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postDto);
        });

        assertThat(exception.getCause()).isInstanceOf(IOException.class);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void save_ShouldThrowRuntimeException_WhenSQLExceptionOccurs() {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setName("Test Post");
        
        // Используем RuntimeException вместо проверяемых исключений
        doThrow(new RuntimeException("DB error", new SQLException("DB error")))
            .when(postRepository).save(any(Post.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postDto);
        });

        assertThat(exception.getCause()).isInstanceOf(SQLException.class);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void findFreeLastPosts_ShouldReturnPostsFromRepository() {
        // Given
        List<Post> expectedPosts = Arrays.asList(
                new Post(1L, "Post 1", "Content 1", null, 5, "tag1"),
                new Post(2L, "Post 2", "Content 2", null, 3, "tag2")
        );
        when(postRepository.findFreeLastPosts()).thenReturn(expectedPosts);

        // When
        List<Post> actualPosts = postService.findFreeLastPosts();

        // Then
        assertThat(actualPosts).isEqualTo(expectedPosts);
        verify(postRepository).findFreeLastPosts();
    }

    @Test
    void findByName_ShouldReturnPostsFromRepository() {
        // Given
        String searchName = "test";
        List<Post> expectedPosts = Arrays.asList(
                new Post(1L, "Test Post", "Content", null, 0, "tag")
        );
        when(postRepository.findByName(searchName)).thenReturn(expectedPosts);

        // When
        List<Post> actualPosts = postService.findByName(searchName);

        // Then
        assertThat(actualPosts).isEqualTo(expectedPosts);
        verify(postRepository).findByName(searchName);
    }

    @Test
    void findById_ShouldReturnPost_WhenPostExists() {
        // Given
        Long postId = 1L;
        Post expectedPost = new Post(postId, "Test Post", "Test Content", null, 5, "tag");
        when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

        // When
        Optional<Post> actualPost = postService.findById(postId);

        // Then
        assertThat(actualPost).isPresent();
        assertThat(actualPost.get()).isEqualTo(expectedPost);
        verify(postRepository).findById(postId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenPostNotExists() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        Optional<Post> actualPost = postService.findById(postId);

        // Then
        assertThat(actualPost).isEmpty();
        verify(postRepository).findById(postId);
    }

    @Test
    void updatePost_ShouldUpdatePost_WhenValidPostDto() throws SQLException, IOException {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setId(1L);
        postDto.setName("Updated Post");
        postDto.setText("Updated Content");

        // When
        postService.updatePost(postDto);

        // Then
        verify(postRepository).updatePost(any(Post.class));
    }




    @Test
    void addLike_ShouldIncrementLikeCount_WhenPostExists() {
        // Given
        Long postId = 1L;
        Post post = new Post(postId, "Test Post", "Test Content", null, 5, "tag");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        postService.addLike(postId);

        // Then
        assertThat(post.getLikeCount()).isEqualTo(6);
        verify(postRepository).findById(postId);
        verify(postRepository).updatePost(post);
    }

    @Test
    void addLike_ShouldThrowRuntimeException_WhenPostNotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.addLike(postId);
        });

        assertThat(exception.getMessage()).isEqualTo("Post not found");
        verify(postRepository).findById(postId);
        verify(postRepository, never()).updatePost(any(Post.class));
    }

    @Test
    void addComment_ShouldSaveComment_WhenPostExists() {
        // Given
        Long postId = 1L;
        CommentRequest commentRequest = new CommentRequest("Test Author", "Test Text");

        Post post = new Post(postId, "Test Post", "Test Content", null, 5, "tag");
        Comment expectedComment = new Comment("Test Text", "Test Author", postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

        // When
        Comment actualComment = postService.addComment(postId, commentRequest);

        // Then
        assertThat(actualComment).isEqualTo(expectedComment);
        verify(postRepository).findById(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowRuntimeException_WhenPostNotFound() {
        // Given
        Long postId = 999L;
        CommentRequest commentRequest = new CommentRequest("Test Author", "Test Text");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.addComment(postId, commentRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Post not found");
        verify(postRepository).findById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void getImageById_ShouldReturnImage_WhenPostExists() {
        // Given
        Long postId = 1L;
        byte[] expectedImage = "test-image".getBytes();
        Post post = new Post(postId, "Test Post", "Test Content", expectedImage, 5, "tag");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        byte[] actualImage = postService.getImageById(postId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
        verify(postRepository).findById(postId);
    }

    @Test
    void getImageById_ShouldThrowRuntimeException_WhenPostNotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.getImageById(postId);
        });

        assertThat(exception.getMessage()).isEqualTo("Post not found");
        verify(postRepository).findById(postId);
    }

    @Test
    void getImageById_ShouldReturnNull_WhenPostHasNoImage() {
        // Given
        Long postId = 1L;
        Post post = new Post(postId, "Test Post", "Test Content", null, 5, "tag");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        byte[] actualImage = postService.getImageById(postId);

        // Then
        assertThat(actualImage).isNull();
        verify(postRepository).findById(postId);
    }
}
