package ru.razzh.igor.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.razzh.igor.dto.CommentResponse;
import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.dto.PostExtendedInDto;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.exception.PostNotFoundException;
import ru.razzh.igor.map.Mapper;
import ru.razzh.igor.service.CommentService;
import ru.razzh.igor.service.PostService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PostController postController;

    @Test
    void getPosts_ShouldReturnPostsView() {
        // When
        String viewName = postController.getPosts();

        // Then
        assertThat(viewName).isEqualTo("posts");
    }

    @Test
    void addComment_ShouldSaveComment() {
        // Given
        Comment comment = new Comment();
        comment.setAuthor("Test Author");
        comment.setText("Test Text");
        comment.setPostId(1L);

        // When
        postController.addComment(comment);

        // Then
        verify(commentService).save(comment);
    }

    @Test
    void addPost_ShouldSavePostAndRedirect() throws IOException, SQLException {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setName("Test Post");
        postDto.setText("Test Content");

        // When
        String redirect = postController.addPost(postDto);

        // Then
        verify(postService).save(postDto);
        assertThat(redirect).isEqualTo("redirect:/blog");
    }

    @Test
    void posts_ShouldReturnListOfPosts() {
        // Given
        List<Post> expectedPosts = Arrays.asList(
                new Post(1L, "Post 1", "Content 1", null, 5, "tag1"),
                new Post(2L, "Post 2", "Content 2", null, 3, "tag2")
        );
        when(postService.findFreeLastPosts()).thenReturn(expectedPosts);

        // When
        ResponseEntity<List<Post>> response = postController.posts();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPosts);
        verify(postService).findFreeLastPosts();
    }

    @Test
    void getImage_ShouldReturnImageBytes() {
        // Given
        byte[] imageBytes = "test-image".getBytes();
        when(postService.getImageById(1L)).thenReturn(imageBytes);

        // When
        ResponseEntity<byte[]> response = postController.getImage(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(imageBytes);
        verify(postService).getImageById(1L);
    }

    @Test
    void searchPostsByName_ShouldReturnPosts() {
        // Given
        String searchName = "test";
        List<Post> expectedPosts = Arrays.asList(
                new Post(1L, "Test Post", "Content", null, 0, "tag")
        );
        when(postService.findByName(searchName)).thenReturn(expectedPosts);

        // When
        ResponseEntity<List<Post>> response = postController.searchPostsByName(searchName);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPosts);
        verify(postService).findByName(searchName);
    }


    @Test
    void viewPost_ShouldThrowException_WhenPostNotFound() {
        // Given
        Long postId = 999L;
        when(postService.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundException.class, () -> {
            postController.viewPost(postId, model);
        });
        verify(postService).findById(postId);
    }

    @Test
    void updatePost_ShouldUpdateAndRedirect() throws IOException, SQLException {
        // Given
        PostExtendedInDto postDto = new PostExtendedInDto();
        postDto.setId(1L);
        postDto.setName("Updated Post");
        postDto.setText("Updated Content");

        // When
        String redirect = postController.updatePost(postDto);

        // Then
        verify(postService).updatePost(postDto);
        assertThat(redirect).isEqualTo("redirect:/post/1");
    }

    @Test
    void addLike_ShouldReturnOk_WhenPostExists() {
        // Given
        Long postId = 1L;

        // When
        ResponseEntity<?> response = postController.addLike(postId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(postService).addLike(postId);
    }

    @Test
    void addCommentWithParams_ShouldSaveCommentAndRedirect() {
        // Given
        Long postId = 1L;
        String author = "Test User";
        String text = "Test Comment";

        List<Comment> comments = Arrays.asList(
                new Comment(text, author, postId)
        );
        when(commentService.getComments(postId)).thenReturn(comments);

        // When
        String redirect = postController.addComment(postId, author, text, redirectAttributes);

        // Then
        assertThat(redirect).isEqualTo("redirect:/post/1");
        verify(commentService).save(any(Comment.class));
        verify(redirectAttributes).addFlashAttribute("comments", comments);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Комментарий добавлен!");
    }

    @Test
    void getComments_ShouldReturnCommentsList() {
        // Given
        Long postId = 1L;
        List<Comment> comments = Arrays.asList(
                new Comment("Great post!", "User1", postId),
                new Comment("Thanks!", "User2", postId)
        );

        when(commentService.getComments(postId)).thenReturn(comments);

        // When
        ResponseEntity<List<CommentResponse>> response = postController.getComments(postId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).author()).isEqualTo("User1");
        assertThat(response.getBody().get(1).author()).isEqualTo("User2");
        verify(commentService).getComments(postId);
    }

    @Test
    void getComments_ShouldReturnNotFound_WhenExceptionOccurs() {
        // Given
        Long postId = 999L;
        when(commentService.getComments(postId)).thenThrow(new RuntimeException("Error"));

        // When
        ResponseEntity<List<CommentResponse>> response = postController.getComments(postId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(commentService).getComments(postId);
    }

    @Test
    void addCommentWithParams_ShouldCreateCommentWithCorrectFields() {
        // Given
        Long postId = 1L;
        String author = "John";
        String text = "Test comment text";

        // When
        postController.addComment(postId, author, text, redirectAttributes);

        // Then
        verify(commentService).save(argThat(comment ->
                comment.getAuthor().equals(author) &&
                        comment.getText().equals(text) &&
                        comment.getPostId().equals(postId) &&
                        comment.getCreatedAt() != null
        ));
    }
}