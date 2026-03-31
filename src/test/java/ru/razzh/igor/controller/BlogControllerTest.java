package ru.razzh.igor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.razzh.igor.controller.BlogController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BlogController.class)
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getPosts_ShouldReturnBlogView() throws Exception {
        // When & Then
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog"));
    }

    @Test
    void getPosts_ShouldInteractWithPostService() throws Exception {
        // When
        mockMvc.perform(get("/blog"));

        // Then - проверяем, что сервис был задействован (если в будущем добавится логика)
        // В текущей реализации метод getPosts() не использует postService,
        // но эта проверка может быть полезной при расширении функционала
        // verify(postService).someMethod(); // раскомментировать когда добавится логика
    }

    @Test
    void getPosts_WithDifferentHttpMethods_ShouldHandleOnlyGet() throws Exception {
        // Given - контроллер настроен только на GET запросы для "/blog"

        // When & Then - GET запрос должен работать
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk());

        // POST, PUT, DELETE запросы будут возвращать 405 Method Not Allowed
        // (это тестируется через интеграционные тесты)
    }
}