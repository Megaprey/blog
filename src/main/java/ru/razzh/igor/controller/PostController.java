package ru.razzh.igor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.exception.PostNotFoundException;
import ru.razzh.igor.service.CommentService;
import ru.razzh.igor.service.PostService;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

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
    public String addPost(@ModelAttribute PostDto postDto) throws IOException, SQLException {
        postService.save(postDto);

        return "redirect:/blog";
    }

    @GetMapping(value = "posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public  @ResponseBody ResponseEntity<List<Post>> posts() {
        return ResponseEntity.ok(postService.findFreeLastPosts());
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<Post>> searchPostsByName(@RequestParam String name) {
        return ResponseEntity.ok(postService.findByName(name));
    }

    // Просмотр конкретного поста
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        model.addAttribute("post", post);
        return "posts";
    }
    @PostMapping(value = "/update/{id}", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public String updatePost(@ModelAttribute PostDto postDto) throws IOException, SQLException {
        postService.updatePost(postDto);
        return "redirect:/post/" + postDto.getId();
    }
}
