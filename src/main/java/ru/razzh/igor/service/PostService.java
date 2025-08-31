package ru.razzh.igor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.razzh.igor.dto.CommentRequest;
import ru.razzh.igor.dto.PostDto;
import ru.razzh.igor.dto.PostExtendedInDto;
import ru.razzh.igor.entity.Comment;
import ru.razzh.igor.entity.Post;
import ru.razzh.igor.map.Mapper;
import ru.razzh.igor.repository.CommentRepository;
import ru.razzh.igor.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public void save(PostExtendedInDto postDto) {
        try {
            postRepository.save(Mapper.postDtoMapToPost(postDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> findFreeLastPosts() {
        return postRepository.findFreeLastPosts();
    }

    public List<Post> findByName(String name) {
        return postRepository.findByName(name);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void updatePost(PostExtendedInDto postDto) throws SQLException, IOException {
        postRepository.updatePost(Mapper.postDtoMapToPost(postDto));
    }
    // Добавление лайка
    @Transactional
    public void addLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.updatePost(post);
    }

    // Добавление комментария
    @Transactional
    public Comment addComment(Long postId, CommentRequest commentRequest) {
        postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment(commentRequest.author(), commentRequest.text(), postId);

        return commentRepository.save(comment);
    }

    public byte[] getImageById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"))
                .getImage();
    }
}
