package ajaajaja.debugging_rounge.common.jwt;

import ajaajaja.debugging_rounge.common.jwt.exception.JwtParsingException;
import ajaajaja.debugging_rounge.common.jwt.exception.JwtValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    JwtProperties jwtProperties;
    JwtProvider jwtProvider;
    String encodedSecret;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();

        JwtProperties.Token tokenProps = new JwtProperties.Token();
        tokenProps.setAccessExpiration(Duration.ofMinutes(30));
        tokenProps.setRefreshExpiration(Duration.ofDays(14));
        tokenProps.setRenewThreshold(Duration.ofHours(24));

        jwtProperties.setToken(tokenProps);

        String rawSecret = "abcdefghijklmnopqrstuvwxyzabcdef"; // 32 chars
        encodedSecret = Base64.getEncoder()
                .encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));

        jwtProvider = new JwtProvider(jwtProperties, encodedSecret);
    }

    @Test
    @DisplayName("createToken + extractSubject")
    void 토큰_생성_및_subject_파싱() {

        // given
        String subject = "123";

        // when
        String accessToken = jwtProvider.createToken(subject, TokenType.ACCESS);
        String subjectFromAccessToken = jwtProvider.extractSubject(accessToken, TokenType.ACCESS);

        String refreshToken = jwtProvider.createToken(subject, TokenType.REFRESH);
        String subjectFromRefreshToken = jwtProvider.extractSubject(refreshToken, TokenType.REFRESH);

        // then
        assertThat(subjectFromAccessToken).isEqualTo(subject);
        assertThat(subjectFromRefreshToken).isEqualTo(subject);
    }

    @Test
    @DisplayName("ACCESS 토큰을 REFRESH 타입으로 검증하면 예외 발생")
    void 토큰_타입_검증() {

        // given
        String subject = "123";
        String accessToken = jwtProvider.createToken(subject, TokenType.ACCESS);

        // when & then
        assertThatThrownBy(() -> jwtProvider.extractSubject(accessToken, TokenType.REFRESH))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    @DisplayName("만료된 토큰 -> 예외 발생")
    void 만료_토큰() {

        // given
        JwtProperties expiredProps = new JwtProperties();
        JwtProperties.Token tokenProps = new JwtProperties.Token();
        tokenProps.setAccessExpiration(Duration.ofSeconds(-1));
        tokenProps.setRefreshExpiration(Duration.ofDays(14));
        tokenProps.setRenewThreshold(Duration.ofHours(24));
        expiredProps.setToken(tokenProps);

        JwtProvider expiredProvider = new JwtProvider(expiredProps, encodedSecret);

        String subject = "123";
        String token = expiredProvider.createToken(subject, TokenType.ACCESS);

        assertThatThrownBy(() -> jwtProvider.extractExpiration(token)).isInstanceOf(JwtValidationException.class);
    }

    @Test
    @DisplayName("형식이 잘못된 토큰 -> 예외 발생")
    void 잘못된_형식_토큰() {
        assertThatThrownBy(() -> jwtProvider.extractSubject("abc.def.ghi", TokenType.ACCESS))
                .isInstanceOf(JwtParsingException.class);
    }

    @Test
    @DisplayName("서명이 잘못된 토큰 -> 예외 발생")
    void 잘못된_서명_토큰() {
        // given
        String anotherEncodedSecret = "01234567890123456789012345678901";
        String encodedSecret1 = Base64.getEncoder()
                .encodeToString(anotherEncodedSecret.getBytes(StandardCharsets.UTF_8));
        JwtProvider creator = new JwtProvider(jwtProperties, encodedSecret1);

        String token = creator.createToken("123", TokenType.ACCESS);

        // when & then
        assertThatThrownBy(() -> jwtProvider.extractSubject(token, TokenType.ACCESS))
                .isInstanceOf(JwtValidationException.class);
    }
}