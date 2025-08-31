package ru.razzh.igor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.razzh.igor.dto.*;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.exception.PostNotFoundException;
import ru.razzh.igor.map.Mapper;
import ru.razzh.igor.service.CommentService;
import ru.razzh.igor.service.PostService;

import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;


    @GetMapping
    public String getPosts() {

        // Передаём данные в виде атрибута users
//        model.addAttribute("posts", posts);

        return "posts"; // Возвращаем название шаблона — users.html
    }

    @PostMapping("/comment/add")
    public void addComment(@ModelAttribute Comment comment) {
        commentService.save(comment);
        // Передаём данные в виде атрибута users
//        model.addAttribute("posts", posts);

//        return "posts"; // Возвращаем название шаблона — users.html
    }

    @PostMapping(value = "/add", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public String addPost(@ModelAttribute PostExtendedInDto postDto) throws IOException, SQLException {
        postService.save(postDto);

        return "redirect:/blog";
    }

    @GetMapping(value = "posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public  @ResponseBody ResponseEntity<List<Post>> posts() {
        return ResponseEntity.ok(postService.findFreeLastPosts());
    }

    @GetMapping(value = "images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public  @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getImageById(id));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<Post>> searchPostsByName(@RequestParam String name) {
        return ResponseEntity.ok(postService.findByName(name));
    }

    // Просмотр конкретного поста
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) throws SQLException, IOException {
        Post post = postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        PostDto postDto = Mapper.postMapToPostDto(post);
        model.addAttribute("post", postDto);
        return "posts";
    }

    @PostMapping(value = "/update/{id}", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public String updatePost(@ModelAttribute PostExtendedInDto postDto) throws IOException, SQLException {
        postService.updatePost(postDto);
        return "redirect:/post/" + postDto.getId();
    }

    // Добавление лайка
    @PostMapping("/{id}/like")
    public ResponseEntity<?> addLike(@PathVariable Long id) {
        try {
            postService.addLike(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Добавление комментария
    @PostMapping("/{postId}/comment")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String author,
                             @RequestParam String text,
                             RedirectAttributes redirectAttributes) {

        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());

        commentService.save(comment);

        redirectAttributes.addFlashAttribute("comments", commentService.getComments(postId));
        redirectAttributes.addFlashAttribute("successMessage", "Комментарий добавлен!");

        return "redirect:/post/" + postId;
    }

    // Получение комментариев
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        try {
            List<Comment> comments = commentService.getComments(id);
            List<CommentResponse> responses = comments.stream()
                    .map(com -> new CommentResponse(com.getId(), com.getAuthor(),
                            com.getText(), com.getPostId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
