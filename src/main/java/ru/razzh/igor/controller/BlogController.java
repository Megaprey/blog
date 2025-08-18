package ru.razzh.igor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.razzh.igor.service.PostService;


@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {
    private final PostService postService;

    @GetMapping
    public String getPosts() {
        return "blog";
    }

}
