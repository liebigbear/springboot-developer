package me.jeontaeung.springbootdeveloper.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jeontaeung.springbootdeveloper.domain.Article;

@NoArgsConstructor
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    @Builder
    public AddArticleRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
