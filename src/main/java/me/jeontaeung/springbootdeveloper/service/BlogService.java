package me.jeontaeung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jeontaeung.springbootdeveloper.domain.Article;
import me.jeontaeung.springbootdeveloper.dto.AddArticleRequest;
import me.jeontaeung.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeontaeung.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;
    //Get
    public List<Article> findAll() {
        return blogRepository.findAll();
    }
    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found:" + id));
    }
    //Post
    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }
    //Patch
    @Transactional
    public Article update(Long id, UpdateArticleRequest request) {
        Article updated = blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found"+id));
        authorizeArticleAuthor(updated);
        updated.update(request.getTitle(), request.getContent());
        return updated;
    }
    //Delete
    public void delete(Long id) {
        Article article = blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found"));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    //게시글 작성 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
