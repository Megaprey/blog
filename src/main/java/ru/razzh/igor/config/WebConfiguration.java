package ru.razzh.igor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.annotation.MultipartConfig;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.razzh.igor")
@PropertySource("classpath:application.properties")
@MultipartConfig(
        location = "/tmp", // Directory where files will be stored temporarily
        fileSizeThreshold = 1024 * 1024, // 1MB - files larger than this are written to disk
        maxFileSize = 1024 * 1024 * 5, // 5MB - maximum size allowed for a single uploaded file
        maxRequestSize = 1024 * 1024 * 5 * 5 // 25MB - maximum size allowed for the entire multipart request
)
public class WebConfiguration {
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

}
