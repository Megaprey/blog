package ru.razzh.igor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.repository.CommentRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void save_ShouldCallRepositorySave() {
        // Given
        Comment comment = new Comment();
        comment.setAuthor("Test Author");
        comment.setText("Test Text");
        comment.setPostId(1L);

        // When
        commentService.save(comment);

        // Then
        verify(commentRepository).save(comment);
    }

    @Test
    void save_ShouldPassCommentToRepository() {
        // Given
        Comment comment = new Comment();
        comment.setAuthor("John");
        comment.setText("Great post!");
        comment.setPostId(5L);

        // When
        commentService.save(comment);

        // Then
        verify(commentRepository).save(comment);
    }

    @Test
    void getComments_ShouldReturnCommentsFromRepository() {
        // Given
        Long postId = 1L;
        List<Comment> expectedComments = Arrays.asList(
                new Comment("Great post!", "User1", postId),
                new Comment("Thanks for sharing", "User2", postId)
        );

        when(commentRepository.findByPost(postId)).thenReturn(expectedComments);

        // When
        List<Comment> actualComments = commentService.getComments(postId);

        // Then
        assertThat(actualComments).isEqualTo(expectedComments);
        verify(commentRepository).findByPost(postId);
    }

    @Test
    void getComments_ShouldReturnEmptyList_WhenNoComments() {
        // Given
        Long postId = 2L;
        when(commentRepository.findByPost(postId)).thenReturn(List.of());

        // When
        List<Comment> comments = commentService.getComments(postId);

        // Then
        assertThat(comments).isEmpty();
        verify(commentRepository).findByPost(postId);
    }

    @Test
    void getComments_ShouldCallRepositoryWithCorrectPostId() {
        // Given
        Long postId = 10L;
        when(commentRepository.findByPost(postId)).thenReturn(List.of());

        // When
        commentService.getComments(postId);

        // Then
        verify(commentRepository).findByPost(postId);
    }

    @Test
    void getComments_ShouldReturnMultipleComments() {
        // Given
        Long postId = 3L;
        List<Comment> expectedComments = Arrays.asList(
                new Comment("First comment", "Alice", postId),
                new Comment("Second comment", "Bob", postId),
                new Comment("Third comment", "Charlie", postId)
        );

        when(commentRepository.findByPost(postId)).thenReturn(expectedComments);

        // When
        List<Comment> actualComments = commentService.getComments(postId);

        // Then
        assertThat(actualComments).hasSize(3);
        assertThat(actualComments)
                .extracting(Comment::getAuthor)
                .containsExactly("Alice", "Bob", "Charlie");
        verify(commentRepository).findByPost(postId);
    }

    @Test
    void save_ShouldHandleCommentWithNullId() {
        // Given
        Comment comment = new Comment();
        comment.setAuthor("Author");
        comment.setText("Text");
        comment.setPostId(1L);
        // id is null - new comment

        // When
        commentService.save(comment);

        // Then
        verify(commentRepository).save(comment);
    }

    @Test
    void save_ShouldHandleCommentWithExistingId() {
        // Given
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setAuthor("Author");
        comment.setText("Updated text");
        comment.setPostId(1L);

        // When
        commentService.save(comment);

        // Then
        verify(commentRepository).save(comment);
    }

    @Test
    void getComments_ShouldReturnCommentsWithCorrectPostId() {
        // Given
        Long postId = 5L;
        List<Comment> repositoryComments = Arrays.asList(
                new Comment("Comment 1", "User1", postId),
                new Comment("Comment 2", "User2", postId)
        );

        when(commentRepository.findByPost(postId)).thenReturn(repositoryComments);

        // When
        List<Comment> serviceComments = commentService.getComments(postId);

        // Then
        assertThat(serviceComments)
                .allMatch(comment -> comment.getPostId().equals(postId));
        verify(commentRepository).findByPost(postId);
    }
}