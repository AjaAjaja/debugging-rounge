package ajaajaja.debugging_rounge.feature.answer.api;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.api.mapper.AnswerMapper;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.DeleteAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.GetAnswersQuery;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.UpdateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerNotFoundException;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerUpdateForbiddenException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTest;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTestSupport;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(AnswerController.class)
@DisplayName("AnswerController 단위 테스트")
class AnswerControllerTest extends WebMvcSecurityTestSupport {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CreateAnswerUseCase createAnswerUseCase;

    @MockitoBean
    GetAnswersQuery getAnswersQuery;

    @MockitoBean
    UpdateAnswerUseCase updateAnswerUseCase;

    @MockitoBean
    DeleteAnswerUseCase deleteAnswerUseCase;

    @MockitoBean
    AnswerMapper answerMapper;

    @Nested
    @DisplayName("POST /questions/{questionId}/answers - 답변 생성")
    class CreateAnswer {

        @Test
        @DisplayName("인증된 사용자가 유효한 데이터로 답변 생성 - 201과 answerId 반환")
        void 답변생성_성공_201() throws Exception {
            // given
            Long questionId = 1L;
            Long userId = 1L;
            Long answerId = 100L;
            String requestBody = """
                    {
                        "content": "테스트 답변 내용입니다."
                    }
                    """;

            given(createAnswerUseCase.createAnswer(any())).willReturn(answerId);

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$").value(answerId));

            verify(createAnswerUseCase).createAnswer(any());
        }

        @Test
        @DisplayName("인증 없이 답변 생성 시도하면 401을 반환한다")
        void 인증없이_답변생성_401() throws Exception {
            // given
            Long questionId = 1L;
            String requestBody = """
                    {
                        "content": "테스트 답변 내용입니다."
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(createAnswerUseCase, never()).createAnswer(any());
        }

        @Test
        @DisplayName("내용이 비어있으면 400을 반환한다")
        void 내용없음_400() throws Exception {
            // given
            Long questionId = 1L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": ""
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createAnswerUseCase, never()).createAnswer(any());
        }

        @Test
        @DisplayName("내용이 5자 미만이면 400을 반환한다")
        void 내용길이_5자미만_400() throws Exception {
            // given
            Long questionId = 1L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": "1234"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createAnswerUseCase, never()).createAnswer(any());
        }

        @Test
        @DisplayName("내용이 10000자를 초과하면 400을 반환한다")
        void 내용길이초과_400() throws Exception {
            // given
            Long questionId = 1L;
            Long userId = 1L;
            String longContent = "a".repeat(10001);
            String requestBody = String.format("""
                    {
                        "content": "%s"
                    }
                    """, longContent);

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(createAnswerUseCase, never()).createAnswer(any());
        }

        @Test
        @DisplayName("존재하지 않는 질문에 답변 작성 시도하면 404를 반환한다")
        void 존재하지않는질문_답변작성_404() throws Exception {
            // given
            Long questionId = 999L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": "테스트 답변 내용입니다."
                    }
                    """;

            given(createAnswerUseCase.createAnswer(any()))
                    .willThrow(new QuestionNotFoundException());

            // when & then
            mockMvc.perform(post("/questions/{questionId}/answers", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());

            verify(createAnswerUseCase).createAnswer(any());
        }
    }

    @Nested
    @DisplayName("GET /questions/{questionId}/answers - 답변 목록 조회")
    class GetAnswerList {

        @Test
        @DisplayName("인증 없이도 답변 목록 조회 가능 - 200 반환 (permitAll)")
        void 인증없이_조회가능_200() throws Exception {
            // given
            Long questionId = 1L;
            Page<AnswerDetailDto> mockPage = new PageImpl<>(List.of());
            given(getAnswersQuery.getAllAnswerByQuestionId(eq(questionId), any()))
                    .willReturn(mockPage);
            given(answerMapper.toResponse(any(AnswerDetailDto.class), isNull()))
                    .willReturn(mock(AnswerDetailResponse.class));

            // when & then
            mockMvc.perform(get("/questions/{questionId}/answers", questionId))
                    .andExpect(status().isOk());

            verify(getAnswersQuery).getAllAnswerByQuestionId(eq(questionId), any());
        }

        @Test
        @DisplayName("인증된 사용자가 답변 조회 - loginUserId가 전달된다")
        void 인증된사용자_조회() throws Exception {
            // given
            Long questionId = 1L;
            Long loginUserId = 5L;
            Page<AnswerDetailDto> mockPage = new PageImpl<>(List.of());
            given(getAnswersQuery.getAllAnswerByQuestionId(eq(questionId), any()))
                    .willReturn(mockPage);
            given(answerMapper.toResponse(any(AnswerDetailDto.class), eq(loginUserId)))
                    .willReturn(mock(AnswerDetailResponse.class));

            // when & then
            mockMvc.perform(get("/questions/{questionId}/answers", questionId)
                            .with(jwt().jwt(jwt -> jwt.subject(loginUserId.toString()))))
                    .andExpect(status().isOk());

            verify(getAnswersQuery).getAllAnswerByQuestionId(eq(questionId), any());
        }

        @Test
        @DisplayName("존재하지 않는 질문의 답변 조회 시 404를 반환한다")
        void 질문없음_404() throws Exception {
            // given
            Long questionId = 999L;
            given(getAnswersQuery.getAllAnswerByQuestionId(eq(questionId), any()))
                    .willThrow(new QuestionNotFoundException());

            // when & then
            mockMvc.perform(get("/questions/{questionId}/answers", questionId))
                    .andExpect(status().isNotFound());

            verify(getAnswersQuery).getAllAnswerByQuestionId(eq(questionId), any());
        }
    }

    @Nested
    @DisplayName("PUT /answers/{answerId} - 답변 수정")
    class UpdateAnswer {

        @Test
        @DisplayName("작성자가 자신의 답변을 수정 - 204 반환")
        void 작성자_수정_204() throws Exception {
            // given
            Long answerId = 1L;
            Long authorId = 1L;
            String requestBody = """
                    {
                        "content": "수정된 답변 내용입니다."
                    }
                    """;

            doNothing().when(updateAnswerUseCase).updateAnswer(any());

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(authorId.toString()))))
                    .andExpect(status().isNoContent());

            verify(updateAnswerUseCase).updateAnswer(any());
        }

        @Test
        @DisplayName("다른 사용자가 답변 수정 시도하면 403을 반환한다")
        void 다른사용자_수정시도_403() throws Exception {
            // given
            Long answerId = 1L;
            Long otherUserId = 999L;
            String requestBody = """
                    {
                        "content": "수정된 답변 내용입니다."
                    }
                    """;

            doThrow(new AnswerUpdateForbiddenException())
                    .when(updateAnswerUseCase).updateAnswer(any());

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(otherUserId.toString()))))
                    .andExpect(status().isForbidden());

            verify(updateAnswerUseCase).updateAnswer(any());
        }

        @Test
        @DisplayName("인증 없이 수정 시도하면 401을 반환한다")
        void 인증없이_수정시도_401() throws Exception {
            // given
            Long answerId = 1L;
            String requestBody = """
                    {
                        "content": "수정된 답변 내용입니다."
                    }
                    """;

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(updateAnswerUseCase, never()).updateAnswer(any());
        }

        @Test
        @DisplayName("존재하지 않는 답변 수정 시 404를 반환한다")
        void 존재하지않는답변_수정_404() throws Exception {
            // given
            Long answerId = 999L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": "수정된 답변 내용입니다."
                    }
                    """;

            doThrow(new AnswerNotFoundException())
                    .when(updateAnswerUseCase).updateAnswer(any());

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("내용이 비어있으면 400을 반환한다")
        void 내용없음_400() throws Exception {
            // given
            Long answerId = 1L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": ""
                    }
                    """;

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(updateAnswerUseCase, never()).updateAnswer(any());
        }

        @Test
        @DisplayName("내용이 5자 미만이면 400을 반환한다")
        void 내용길이_5자미만_400() throws Exception {
            // given
            Long answerId = 1L;
            Long userId = 1L;
            String requestBody = """
                    {
                        "content": "1234"
                    }
                    """;

            // when & then
            mockMvc.perform(put("/answers/{answerId}", answerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isBadRequest());

            verify(updateAnswerUseCase, never()).updateAnswer(any());
        }
    }

    @Nested
    @DisplayName("DELETE /answers/{answerId} - 답변 삭제")
    class DeleteAnswer {

        @Test
        @DisplayName("작성자가 자신의 답변을 삭제 - 204 반환")
        void 작성자_삭제_204() throws Exception {
            // given
            Long answerId = 1L;
            Long authorId = 1L;

            doNothing().when(deleteAnswerUseCase).deleteAnswer(answerId, authorId);

            // when & then
            mockMvc.perform(delete("/answers/{answerId}", answerId)
                            .with(jwt().jwt(jwt -> jwt.subject(authorId.toString()))))
                    .andExpect(status().isNoContent());

            verify(deleteAnswerUseCase).deleteAnswer(answerId, authorId);
        }

        @Test
        @DisplayName("다른 사용자가 답변 삭제 시도하면 403을 반환한다")
        void 다른사용자_삭제시도_403() throws Exception {
            // given
            Long answerId = 1L;
            Long otherUserId = 999L;

            doThrow(new AnswerUpdateForbiddenException())
                    .when(deleteAnswerUseCase).deleteAnswer(answerId, otherUserId);

            // when & then
            mockMvc.perform(delete("/answers/{answerId}", answerId)
                            .with(jwt().jwt(jwt -> jwt.subject(otherUserId.toString()))))
                    .andExpect(status().isForbidden());

            verify(deleteAnswerUseCase).deleteAnswer(answerId, otherUserId);
        }

        @Test
        @DisplayName("인증 없이 삭제 시도하면 401을 반환한다")
        void 인증없이_삭제시도_401() throws Exception {
            // given
            Long answerId = 1L;

            // when & then
            mockMvc.perform(delete("/answers/{answerId}", answerId))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

            verify(deleteAnswerUseCase, never()).deleteAnswer(anyLong(), anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 답변 삭제 시 404를 반환한다")
        void 존재하지않는답변_삭제_404() throws Exception {
            // given
            Long answerId = 999L;
            Long userId = 1L;

            doThrow(new AnswerNotFoundException())
                    .when(deleteAnswerUseCase).deleteAnswer(answerId, userId);

            // when & then
            mockMvc.perform(delete("/answers/{answerId}", answerId)
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());

            verify(deleteAnswerUseCase).deleteAnswer(answerId, userId);
        }
    }
}

