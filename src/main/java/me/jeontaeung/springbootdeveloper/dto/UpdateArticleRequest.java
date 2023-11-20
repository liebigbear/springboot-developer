package me.jeontaeung.springbootdeveloper.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;

@NoArgsConstructor
@Getter
public class UpdateArticleRequest {
    private String title;
    private String content;

    @Builder
    public UpdateArticleRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
