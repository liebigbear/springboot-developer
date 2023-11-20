package me.jeontaeung.springbootdeveloper.repository;

import me.jeontaeung.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
