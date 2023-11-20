package me.jeontaeung.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import me.jeontaeung.springbootdeveloper.domain.User;
import me.jeontaeung.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken : 유저정보와 만료기간을 전달해 토큰만듦")
    @Test
    void generateToken() {
        //given
        User testUser = userRepository.save(User.builder()
                        .email("user@gmail.com")
                        .password("test")
                .build());
        //when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }
    @DisplayName("vaildToken : 만료된 토큰인 때 유효성 검증 실패")
    @Test
    void validToken() {
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);
        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isFalse();
    }

    @DisplayName("getAuthentication : 토큰 기반 인증 정보 가져올수있다")
    @Test
    void getAuthentication() {
        //given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);
        //when
        Authentication authentication = tokenProvider.getAuthentication(token);
        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId : 검증 테스트")
    @Test
    void getUserId() {
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);
        //when
        Long userIdByToken = tokenProvider.getUserId(token);
        //then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}