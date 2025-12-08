package ajaajaja.debugging_rounge.feature.auth.api;

import ajaajaja.debugging_rounge.common.jwt.JwtProperties;
import ajaajaja.debugging_rounge.common.jwt.JwtProvider;
import ajaajaja.debugging_rounge.common.jwt.TokenType;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.LogoutUseCase;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.ReissueTokensUseCase;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(AuthController.class)
@Import({RefreshCookieFactory.class, JwtProperties.class, JwtProvider.class})
@DisplayName("AuthController 슬라이스 테스트")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    JwtProvider jwtProvider;

    // AuthController는 실제 JWT 검증이 필요하므로 WebMvcSecurityTestSupport를 상속받지 않음
    // (AuthenticationManager를 목킹하지 않음)
    // 대신 필요한 것만 개별적으로 목킹
    @MockitoBean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    @MockitoBean
    AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean
    ReissueTokensUseCase reissueTokensUseCase;

    @MockitoBean
    LogoutUseCase logoutUseCase;

    @Nested
    @DisplayName("POST /auth/refresh - 토큰 재발급")
    class Refresh {

        @Test
        @DisplayName("유효한 JWT로 토큰 재발급 성공 → 200과 accessToken 반환")
        void 토큰재발급_성공() throws Exception {
            // given
            Long userId = 1L;
            // 요청받은 리프레쉬 토큰
            String refreshToken = jwtProvider.createToken(userId.toString(), TokenType.REFRESH);

            // 새로 생성할 리프레쉬 토큰
            String newAccessToken = jwtProvider.createToken(userId.toString(), TokenType.ACCESS);
            String newRefreshToken = jwtProvider.createToken(userId.toString(), TokenType.REFRESH);

            TokenPair tokenPair = new TokenPair(newAccessToken, newRefreshToken);
            given(reissueTokensUseCase.reissueTokens(refreshToken, userId))
                    .willReturn(tokenPair);

            // when & then
            Cookie refreshTokenCookie = new Cookie(
                    jwtProperties.getCookie().getName(),  // 설정값에서 쿠키 이름 가져오기
                    refreshToken
            );
            
            mockMvc.perform(post("/auth/refresh")
                            .cookie(refreshTokenCookie))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            containsString(jwtProperties.getCookie().getName() + "=" + newRefreshToken)));

            verify(reissueTokensUseCase).reissueTokens(refreshToken, userId);
        }

        @Test
        @DisplayName("쿠키 없이 요청하면 401을 반환한다")
        void 쿠키없이_요청_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_NOT_FOUND"));

            verify(reissueTokensUseCase, never()).reissueTokens(anyString(), anyLong());
        }

        @Test
        @DisplayName("유효하지 않은 JWT 쿠키로 요청하면 401을 반환한다")
        void 유효하지않은JWT_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh")
                            .cookie(new Cookie("refreshToken", "invalid.jwt.token")))
                    .andExpect(status().isUnauthorized());

            verify(reissueTokensUseCase, never()).reissueTokens(anyString(), anyLong());
        }
    }
    @Nested
    @DisplayName("POST /auth/logout - 로그아웃")
    class Logout {

        @Test
        @DisplayName("로그아웃에 성공하면 쿠키 만료 응답을 반환한다")
        void 로그아웃_UseCase호출_쿠키만료() throws Exception {
            // given
            Long userId = 1L;
            String refreshToken = jwtProvider.createToken(userId.toString(), TokenType.REFRESH);
            doNothing().when(logoutUseCase).logout(refreshToken);

            // when & then
            // Authorization 헤더에 실제 REFRESH 토큰을 넣어 refreshManager가 작동하는지 확인
            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isNoContent())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            containsString(jwtProperties.getCookie().getName() + "=")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            containsString("Path=" + jwtProperties.getCookie().getPath())));

            verify(logoutUseCase).logout(refreshToken);
        }

        @Test
        @DisplayName("인증 없이 로그아웃 시도하면 401을 반환한다")
        void 인증없이_로그아웃_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/logout"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_NOT_FOUND"));

            verify(logoutUseCase, never()).logout(anyString());
        }
        @Test
        @DisplayName("유효하지 않은 JWT로 로그아웃 시도하면 401을 반환한다")
        void 유효하지않은JWT_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", "Bearer invalid.jwt.token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(logoutUseCase, never()).logout(anyString());
        }

    }
}
