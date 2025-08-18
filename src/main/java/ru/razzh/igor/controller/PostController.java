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
    public String posts(Model model) {

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
    @ResponseBody
    public String addPost(@ModelAttribute PostDto postDto) throws IOException, SQLException {
        postService.save(postDto);
        Post post1 = postService.findAll().stream().findFirst().get();
        Post post = postService.findByName(postDto.getName()).get();

        String imageBase64 = "";
//        if (postDto.getImage() != null && !postDto.getImage().isEmpty()) {
//            byte[] imageBytes = postDto.getImage().getBytes();
//            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
//        }
        byte[] imageBlob = post.getImage();
        if (imageBlob != null) {


                imageBase64 = Base64.getEncoder().encodeToString(imageBlob);

        }
        return "<h2>ID: " + post.getId() + "</h2>\n" +
                "<h2>Name: " + post.getName() + "</h2>\n" +
                "<h2>Text: " + post.getText() + "</h2>\n" +
                "<h2>Likes: " + post.getLikeCount() + "</h2>\n" +
                "<h2>Tags: " + post.getTags() + "</h2>\n" +
                (imageBase64.isEmpty() ? "" :
                        "<img src='data:image/jpeg;base64," + imageBase64 + "' " +
                                "style='max-width: 300px; margin-top: 20px;'/>");
    }

    @GetMapping(value = "posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public  @ResponseBody ResponseEntity<List<Post>> posts() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public String posts(@PathVariable String id, Model model) {
        return "posts";
    }
}
