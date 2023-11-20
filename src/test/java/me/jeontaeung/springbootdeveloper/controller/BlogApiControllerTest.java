package me.jeontaeung.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jeontaeung.springbootdeveloper.domain.Article;
import me.jeontaeung.springbootdeveloper.domain.User;
import me.jeontaeung.springbootdeveloper.dto.AddArticleRequest;
import me.jeontaeung.springbootdeveloper.dto.ArticleResponse;
import me.jeontaeung.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeontaeung.springbootdeveloper.repository.BlogRepository;
import me.jeontaeung.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                        .password("test")
                .build());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }
    @DisplayName("addArticle 게시물 생성")
    @Test
    public void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest request = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(request);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");
        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));
        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }
    @DisplayName("블로그 글 조회")
    @Test
    void findAllArticle() throws Exception {
        //given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();
        String title = "title";
        String content = "content";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));
        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }
    @DisplayName("글 한개 찾기")
    @Test
    void findArticle() throws Exception{
        //given
        String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));
        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }
    
    @DisplayName("글 삭제")
    @Test
    void deleteArticle() throws Exception{
        //given
        String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        //when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());
        //then
        List<Article> articleList = blogRepository.findAll();
        assertThat(articleList).isEmpty();
    }
    @DisplayName("게시글 수정")
    @Test
    void updateArticle() throws Exception {
        //given
        String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        String newTitle = "newTitle";
        String newContent = "newContent";

        UpdateArticleRequest request = UpdateArticleRequest.builder()
                .title(newTitle)
                .content(newContent)
                .build();
        //when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));
        //then
        result.andExpect(status().isOk());
        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }
    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                        .title("title")
                        .author(user.getUsername())
                        .content("content")
                .build());
    }
}