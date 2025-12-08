package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionWithAnswerResponse;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionResponseMapper;
import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionDeleteForbiddenException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionUpdateForbiddenException;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTest;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(QuestionController.class)
@DisplayName("QuestionController 단위 테스트")
class QuestionControllerTest extends WebMvcSecurityTestSupport {

    @Autowired
    MockMvc mockMvc;

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
    @DisplayName("POST /questions - 질문 생성")
    class CreateQuestion {

        @Test
        @DisplayName("인증된 사용자가 유효한 데이터로 질문 생성 - 201과 questionId 반환")
        void 질문생성_성공_201() throws Exception {
            // given
            Long userId = 1L;
            Long questionId = 100L;
            String requestBody = """
                    {
                        "title": "테스트 질문",
                        "content": "테스트 내용입니다."
                    }
                    """;

            given(createQuestionUseCase.createQuestion(any())).willReturn(questionId);

            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$").value(questionId));

            verify(createQuestionUseCase).createQuestion(any());
        }

        @Test
        @DisplayName("인증 없이 질문 생성 시도하면 401을 반환한다")
        void 인증없이_질문생성_401() throws Exception {
            // given
            String requestBody = """
                    {
                        "title": "테스트 질문",
                        "content": "테스트 내용입니다."
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(createQuestionUseCase, never()).createQuestion(any());
        }

        @Test
        @DisplayName("제목이 비어있으면 400을 반환한다")
        void 제목없음_400() throws Exception {
            // given
            Long userId = 1L;
            String requestBody = """
                    {
                        "title": "",
                        "content": "테스트 내용입니다."
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createQuestionUseCase, never()).createQuestion(any());
        }

        @Test
        @DisplayName("내용이 비어있으면 400을 반환한다")
        void 내용없음_400() throws Exception {
            // given
            Long userId = 1L;
            String requestBody = """
                    {
                        "title": "테스트 질문",
                        "content": ""
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createQuestionUseCase, never()).createQuestion(any());
        }

        @Test
        @DisplayName("제목이 50자를 초과하면 400을 반환한다")
        void 제목길이초과_400() throws Exception {
            // given
            Long userId = 1L;
            String longTitle = "a".repeat(51);
            String requestBody = String.format("""
                    {
                        "title": "%s",
                        "content": "테스트 내용입니다."
                    }
                    """, longTitle);

            // when & then
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createQuestionUseCase, never()).createQuestion(any());
        }
    }

    @Nested
    @DisplayName("GET /questions/{questionId} - 질문 상세 조회")
    class GetQuestionDetail {

        @Test
        @DisplayName("인증 없이도 질문 상세 조회 가능 - 200 반환 (permitAll)")
        void 인증없이_조회가능_200() throws Exception {
            // given
            Long questionId = 1L;
            QuestionWithAnswersDto mockDto = new QuestionWithAnswersDto(
                    questionId, "제목", "내용", 1L, "author@example.com",
                    null, 0, Page.empty()
            );
            QuestionWithAnswerResponse mockResponse = mock(QuestionWithAnswerResponse.class);

            given(getQuestionWithAnswersQuery.getQuestionWithAnswers(eq(questionId), isNull(), any()))
                    .willReturn(mockDto);
            given(questionResponseMapper.toQuestionWithAnswersResponse(mockDto, null))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/questions/{questionId}", questionId))
                    .andExpect(status().isOk());

            verify(getQuestionWithAnswersQuery).getQuestionWithAnswers(eq(questionId), isNull(), any());
        }

        @Test
        @DisplayName("인증된 사용자가 질문 조회 - loginUserId가 전달된다")
        void 인증된사용자_조회() throws Exception {
            // given
            Long questionId = 1L;
            Long loginUserId = 5L;
            QuestionWithAnswersDto mockDto = new QuestionWithAnswersDto(
                    questionId, "제목", "내용", 1L, "author@example.com",
                    null, 0, Page.empty()
            );
            QuestionWithAnswerResponse mockResponse = mock(QuestionWithAnswerResponse.class);

            given(getQuestionWithAnswersQuery.getQuestionWithAnswers(eq(questionId), eq(loginUserId), any()))
                    .willReturn(mockDto);
            given(questionResponseMapper.toQuestionWithAnswersResponse(mockDto, loginUserId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/questions/{questionId}", questionId)
                            .with(jwt().jwt(jwt -> jwt.subject(loginUserId.toString()))))
                    .andExpect(status().isOk());

            verify(getQuestionWithAnswersQuery).getQuestionWithAnswers(eq(questionId), eq(loginUserId), any());
        }

        @Test
        @DisplayName("존재하지 않는 질문 조회 시 404를 반환한다")
        void 질문없음_404() throws Exception {
            // given
            Long questionId = 999L;
            given(getQuestionWithAnswersQuery.getQuestionWithAnswers(eq(questionId), any(), any()))
                    .willThrow(new QuestionNotFoundException());

            // when & then
            mockMvc.perform(get("/questions/{questionId}", questionId))
                    .andExpect(status().isNotFound());

            verify(getQuestionWithAnswersQuery).getQuestionWithAnswers(eq(questionId), any(), any());
        }
    }

    @Nested
    @DisplayName("GET /questions - 질문 목록 조회")
    class GetQuestionList {

        @Test
        @DisplayName("인증 없이도 질문 목록 조회 가능 - 200 반환 (permitAll)")
        void 인증없이_목록조회_200() throws Exception {
            // given
            Page<QuestionListDto> mockPage = new PageImpl<>(List.of());
            given(getQuestionListWithPreviewQuery.getQuestionsWithPreview(any(), eq(QuestionOrder.LATEST)))
                    .willReturn(mockPage);
            given(questionResponseMapper.toQuestionListResponse(any()))
                    .willReturn(mock(QuestionListResponse.class));

            // when & then
            mockMvc.perform(get("/questions"))
                    .andExpect(status().isOk());

            verify(getQuestionListWithPreviewQuery).getQuestionsWithPreview(any(), eq(QuestionOrder.LATEST));
        }

        @Test
        @DisplayName("order 파라미터로 RECOMMEND 지정 시 추천순 조회")
        void 추천순_조회() throws Exception {
            // given
            Page<QuestionListDto> mockPage = new PageImpl<>(List.of());
            given(getQuestionListWithPreviewQuery.getQuestionsWithPreview(any(), eq(QuestionOrder.RECOMMEND)))
                    .willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/questions")
                            .param("order", "RECOMMEND"))
                    .andExpect(status().isOk());

            verify(getQuestionListWithPreviewQuery).getQuestionsWithPreview(any(), eq(QuestionOrder.RECOMMEND));
        }

        @Test
        @DisplayName("order 파라미터 없으면 기본값(LATEST) 사용")
        void 기본값_LATEST() throws Exception {
            // given
            Page<QuestionListDto> mockPage = new PageImpl<>(List.of());
            given(getQuestionListWithPreviewQuery.getQuestionsWithPreview(any(), eq(QuestionOrder.LATEST)))
                    .willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/questions"))
                    .andExpect(status().isOk());

            verify(getQuestionListWithPreviewQuery).getQuestionsWithPreview(any(), eq(QuestionOrder.LATEST));
        }
    }

    @Nested
    @DisplayName("PUT /questions/{questionId} - 질문 수정")
    class UpdateQuestion {

        @Test
        @DisplayName("작성자가 자신의 질문을 수정 - 204 반환")
        void 작성자_수정_204() throws Exception {
            // given
            Long questionId = 1L;
            Long authorId = 1L;
            String requestBody = """
                    {
                        "title": "수정된 제목",
                        "content": "수정된 내용"
                    }
                    """;

            doNothing().when(updateQuestionUseCase).updateQuestion(any());

            // when & then
            mockMvc.perform(put("/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(authorId.toString()))))
                    .andExpect(status().isNoContent());

            verify(updateQuestionUseCase).updateQuestion(any());
        }

        @Test
        @DisplayName("다른 사용자가 질문 수정 시도하면 403을 반환한다")
        void 다른사용자_수정시도_403() throws Exception {
            // given
            Long questionId = 1L;
            Long otherUserId = 999L;
            String requestBody = """
                    {
                        "title": "수정된 제목",
                        "content": "수정된 내용"
                    }
                    """;

            doThrow(new QuestionUpdateForbiddenException())
                    .when(updateQuestionUseCase).updateQuestion(any());

            // when & then
            mockMvc.perform(put("/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(otherUserId.toString()))))
                    .andExpect(status().isForbidden());

            verify(updateQuestionUseCase).updateQuestion(any());
        }

        @Test
        @DisplayName("인증 없이 수정 시도하면 401을 반환한다")
        void 인증없이_수정시도_401() throws Exception {
            // given
            Long questionId = 1L;
            String requestBody = """
                    {
                        "title": "수정된 제목",
                        "content": "수정된 내용"
                    }
                    """;

            // when & then
            mockMvc.perform(put("/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(updateQuestionUseCase, never()).updateQuestion(any());
        }

        @Test
        @DisplayName("존재하지 않는 질문 수정 시 404를 반환한다")
        void 존재하지않는질문_수정_404() throws Exception {
            // given
            Long questionId = 999L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "title": "수정된 제목",
                        "content": "수정된 내용"
                    }
                    """;

            doThrow(new QuestionNotFoundException())
                    .when(updateQuestionUseCase).updateQuestion(any());

            // when & then
            mockMvc.perform(put("/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("제목이 비어있으면 400을 반환한다")
        void 제목없음_400() throws Exception {
            // given
            Long questionId = 1L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "title": "",
                        "content": "수정된 내용"
                    }
                    """;

            // when & then
            mockMvc.perform(put("/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(updateQuestionUseCase, never()).updateQuestion(any());
        }
    }

    @Nested
    @DisplayName("DELETE /questions/{questionId} - 질문 삭제")
    class DeleteQuestion {

        @Test
        @DisplayName("작성자가 자신의 질문을 삭제 - 204 반환")
        void 작성자_삭제_204() throws Exception {
            // given
            Long questionId = 1L;
            Long authorId = 1L;

            doNothing().when(deleteQuestionUseCase).deleteQuestion(questionId, authorId);

            // when & then
            mockMvc.perform(delete("/questions/{questionId}", questionId)
                            .with(jwt().jwt(jwt -> jwt.subject(authorId.toString()))))
                    .andExpect(status().isNoContent());

            verify(deleteQuestionUseCase).deleteQuestion(questionId, authorId);
        }

        @Test
        @DisplayName("다른 사용자가 질문 삭제 시도하면 403을 반환한다")
        void 다른사용자_삭제시도_403() throws Exception {
            // given
            Long questionId = 1L;
            Long otherUserId = 999L;

            doThrow(new QuestionDeleteForbiddenException())
                    .when(deleteQuestionUseCase).deleteQuestion(questionId, otherUserId);

            // when & then
            mockMvc.perform(delete("/questions/{questionId}", questionId)
                            .with(jwt().jwt(jwt -> jwt.subject(otherUserId.toString()))))
                    .andExpect(status().isForbidden());

            verify(deleteQuestionUseCase).deleteQuestion(questionId, otherUserId);
        }

        @Test
        @DisplayName("인증 없이 삭제 시도하면 401을 반환한다")
        void 인증없이_삭제시도_401() throws Exception {
            // given
            Long questionId = 1L;

            // when & then
            mockMvc.perform(delete("/questions/{questionId}", questionId))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(deleteQuestionUseCase, never()).deleteQuestion(anyLong(), anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 질문 삭제 시 404를 반환한다")
        void 존재하지않는질문_삭제_404() throws Exception {
            // given
            Long questionId = 999L;
            Long userId = 1L;

            doThrow(new QuestionNotFoundException())
                    .when(deleteQuestionUseCase).deleteQuestion(questionId, userId);

            // when & then
            mockMvc.perform(delete("/questions/{questionId}", questionId)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());

            verify(deleteQuestionUseCase).deleteQuestion(questionId, userId);
        }
    }
}

