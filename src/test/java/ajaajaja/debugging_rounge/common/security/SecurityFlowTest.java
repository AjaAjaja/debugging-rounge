package ajaajaja.debugging_rounge.common.security;

import ajaajaja.debugging_rounge.feature.auth.api.AuthController;
import ajaajaja.debugging_rounge.feature.auth.api.RefreshCookieFactory;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.LogoutUseCase;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.ReissueTokensUseCase;
import ajaajaja.debugging_rounge.feature.question.api.QuestionController;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionResponseMapper;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTest;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest({
        AuthController.class,
        QuestionController.class
})
public class SecurityFlowTest extends WebMvcSecurityTestSupport {

    @Autowired
    MockMvc mockMvc;

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
    @DisplayName("인증 메커니즘 검증")
    class AuthenticationMechanism {

        @Test
        @DisplayName("인증이 필요한 엔드포인트에 토큰 없이 접근하면 401 + AUTHENTICATION_FAILED를 반환한다")
        void 토큰없이접근_401() throws Exception {
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
        @DisplayName("Bearer 없이 토큰만 전달하면 401 + AUTHENTICATION_FAILED를 반환한다")
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
        @DisplayName("빈 Bearer 토큰으로 접근하면 401 + AUTHENTICATION_FAILED를 반환한다")
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
    @DisplayName("RefreshToken 흐름 검증")
    class RefreshTokenFlow {

        @Test
        @DisplayName("RefreshToken 없이 /auth/refresh에 요청하면 401 + REFRESH_TOKEN_NOT_FOUND를 반환한다")
        void RefreshToken없이요청_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_NOT_FOUND"));
        }

        @Test
        @DisplayName("유효하지 않은 RefreshToken으로 요청하면 401을 반환한다")
        void 유효하지않은RefreshToken_401() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/refresh")
                            .cookie(new Cookie("refreshToken", "invalid-refresh-token")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
        }
    }

    @Nested
    @DisplayName("permitAll 정책 검증")
    class PermitAllPolicy {

        @Test
        @DisplayName("GET /questions/** 패턴은 인증 없이 접근 가능하다")
        void GET_questions_permitAll() throws Exception {
            // given
            Page<QuestionListDto> emptyPage = new PageImpl<>(List.of());
            given(getQuestionListWithPreviewQuery.getQuestionsWithPreview(any(), any()))
                    .willReturn(emptyPage);

            // when & then
            mockMvc.perform(get("/questions"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /questions/{id} 상세 조회도 인증 없이 접근 가능하다")
        void GET_questions_id_permitAll() throws Exception {
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
    @DisplayName("로그아웃 정책 검증")
    class LogoutPolicy {

        @Test
        @DisplayName("POST /auth/logout은 인증이 필요하다")
        void 로그아웃_인증필요() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/logout"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }
    }
}
