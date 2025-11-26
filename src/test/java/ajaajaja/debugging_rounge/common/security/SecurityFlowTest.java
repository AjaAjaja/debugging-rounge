package ajaajaja.debugging_rounge.common.security;

import ajaajaja.debugging_rounge.common.config.security.SecurityConfig;
import ajaajaja.debugging_rounge.common.jwt.JwtConfig;
import ajaajaja.debugging_rounge.common.security.handler.CustomAccessDeniedHandler;
import ajaajaja.debugging_rounge.common.security.handler.CustomAuthenticationEntryPoint;
import ajaajaja.debugging_rounge.common.security.token.CustomBearerTokenResolver;
import ajaajaja.debugging_rounge.feature.auth.api.AuthController;
import ajaajaja.debugging_rounge.feature.auth.api.RefreshCookieFactory;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.LogoutUseCase;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.ReissueTokensUseCase;
import ajaajaja.debugging_rounge.feature.question.api.QuestionController;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionResponseMapper;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AuthController.class,
        QuestionController.class
})
@Import({
        JwtConfig.class,
        SecurityConfig.class,
        CustomBearerTokenResolver.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@ActiveProfiles("test")
public class SecurityFlowTest {

    @Autowired
    MockMvc mockMvc;

    // SecurityConfig 의존성
    @MockitoBean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    @MockitoBean
    AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean(name = "accessAuthenticationManager")
    AuthenticationManager accessAuthenticationManager;

    @MockitoBean(name = "refreshAuthenticationManager")
    AuthenticationManager refreshAuthenticationManager;

    // AuthController 의존성
    @MockitoBean
    ReissueTokensUseCase reissueTokensUseCase;

    @MockitoBean
    LogoutUseCase logoutUseCase;

    @MockitoBean
    RefreshCookieFactory refreshCookieFactory;

    // QuestionController 의존성
    @MockitoBean
    CreateQuestionUseCase createQuestionUseCase;

    @MockitoBean
    GetQuestionWithAnswersQuery getQuestionWithAnswersQuery;

    @MockitoBean
    GetQuestionListWithPreviewQuery getQuestionListWithPreviewQuery;

    @MockitoBean
    UpdateQuestionUseCase updateQuestionUseCase;

    @MockitoBean
    DeleteQuestionUseCase deleteQuestionUseCase;

    @MockitoBean
    QuestionResponseMapper questionResponseMapper;

    @Nested
    @DisplayName("인증 없이 접근하는 경우")
    class WithoutAuthentication {

        @Test
        @DisplayName("질문 생성 API에 토큰 없이 접근하면 401 + AUTHENTICATION_FAILED를 반환한다")
        void 질문생성_토큰없이_401() throws Exception {
            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "test",
                                      "content": "abcdefghijklmnopqrstuvwxyz"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }

        @Test
        @DisplayName("질문 수정 API에 토큰 없이 접근하면 401 + AUTHENTICATION_FAILED를 반환한다")
        void 질문수정_토큰없이_401() throws Exception {
            // when & then
            mockMvc.perform(patch("/questions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "updated",
                                      "content": "updated content"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }

        @Test
        @DisplayName("질문 삭제 API에 토큰 없이 접근하면 401 + AUTHENTICATION_FAILED를 반환한다")
        void 질문삭제_토큰없이_401() throws Exception {
            // when & then
            mockMvc.perform(delete("/questions/1"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("잘못된 토큰으로 접근하는 경우")
    class WithInvalidToken {

        @Test
        @DisplayName("Bearer 없이 토큰만 전달하면 401을 반환한다")
        void Bearer없이토큰_401() throws Exception {
            // when & then
            mockMvc.perform(post("/questions")
                            .header("Authorization", "some-token-without-bearer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "test",
                                      "content": "abcdefghijklmnopqrstuvwxyz"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }

        @Test
        @DisplayName("유효하지 않은 JWT로 접근하면 401을 반환한다")
        void 유효하지않은JWT_401() throws Exception {
            // when & then
            mockMvc.perform(post("/questions")
                            .header("Authorization", "Bearer invalid.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "test",
                                      "content": "abcdefghijklmnopqrstuvwxyz"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
        }

        @Test
        @DisplayName("빈 Bearer 토큰으로 접근하면 401을 반환한다")
        void 빈Bearer토큰_401() throws Exception {
            // when & then
            mockMvc.perform(post("/questions")
                            .header("Authorization", "Bearer ")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "test",
                                      "content": "abcdefghijklmnopqrstuvwxyz"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("Refresh Token 관련 테스트")
    class RefreshTokenTests {

        @Test
        @DisplayName("refreshToken 없이 /auth/refresh에 요청하면 401 + REFRESH_TOKEN_NOT_FOUND를 반환한다")
        void refreshToken없이_요청하면_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_NOT_FOUND"));
        }

        @Test
        @DisplayName("유효하지 않은 refreshToken으로 요청하면 401을 반환한다")
        void 유효하지않은RefreshToken_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh")
                            .cookie(new Cookie("refreshToken", "invalid-refresh-token")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
        }
    }

    @Nested
    @DisplayName("permitAll 엔드포인트 테스트")
    class PermitAllEndpoints {

        @Test
        @DisplayName("GET /questions는 인증 없이 200 OK를 반환한다")
        void 질문목록조회_인증없이_200() throws Exception {
            // given
            Page<QuestionListDto> emptyPage = new PageImpl<>(List.of());
            given(getQuestionListWithPreviewQuery.getQuestionsWithPreview(any(), any()))
                    .willReturn(emptyPage);

            // when & then
            mockMvc.perform(get("/questions"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /questions/{id}는 인증 없이 200 OK를 반환한다")
        void 질문상세조회_인증없이_200() throws Exception {
            // given
            QuestionWithAnswersDto mockDto = new QuestionWithAnswersDto(
                    1L, "title", "content", 1L, "test@example.com",
                    null, 0, Page.empty()
            );
            given(getQuestionWithAnswersQuery.getQuestionWithAnswers(anyLong(), any(), any()))
                    .willReturn(mockDto);

            // when & then
            mockMvc.perform(get("/questions/1"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTests {

        @Test
        @DisplayName("POST /auth/logout은 인증 없이 접근하면 401을 반환한다")
        void 로그아웃_인증없이_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/logout"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }
    }
}
