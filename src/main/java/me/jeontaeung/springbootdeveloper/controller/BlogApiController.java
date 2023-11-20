package me.jeontaeung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.jeontaeung.springbootdeveloper.domain.Article;
import me.jeontaeung.springbootdeveloper.dto.AddArticleRequest;
import me.jeontaeung.springbootdeveloper.dto.ArticleResponse;
import me.jeontaeung.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeontaeung.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    //Get
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticle() {
        List<ArticleResponse> show = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(show);
    }
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable Long id) {
        Article show = blogService.findById(id);
        return ResponseEntity.ok().body(new ArticleResponse(show));
    }
    //Post
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
        Article saved = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    //Patch
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequest request) {
        Article updated = blogService.update(id, request);
        return ResponseEntity.ok().body(updated);
    }
    //Delete
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }
}
