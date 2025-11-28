package ajaajaja.debugging_rounge.feature.answer.application;

import ajaajaja.debugging_rounge.common.security.validator.OwnershipValidator;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerUpdateDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.DeleteAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerNotFoundException;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerNotFoundForDeleteException;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerUpdateForbiddenException;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.QuestionDeleteForbiddenException;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerFacadeTest {

    @Mock
    SaveAnswerPort saveAnswerPort;

    @Mock
    LoadQuestionPort loadQuestionPort;

    @Mock
    LoadAnswerPort loadAnswerPort;

    @Mock
    DeleteAnswerPort deleteAnswerPort;

    @Mock
    OwnershipValidator ownershipValidator;

    @InjectMocks
    AnswerFacade answerFacade;

    @Nested
    @DisplayName("답변 생성 테스트")
    class CreateAnswerTests {

        @Test
        @DisplayName("정상적으로 답변을 생성한다")
        void createAnswer_성공() {
            // given
            Long questionId = 1L;
            Long authorId = 100L;
            String content = "test content";
            AnswerCreateDto createDto = AnswerCreateDto.of(content, questionId, authorId);

            Answer savedAnswer = mock(Answer.class);
            when(savedAnswer.getId()).thenReturn(1L);

            when(loadQuestionPort.existsQuestionById(questionId)).thenReturn(true);
            when(saveAnswerPort.save(any(Answer.class))).thenReturn(savedAnswer);

            // when
            Long answerId = answerFacade.createAnswer(createDto);

            // then
            assertThat(answerId).isEqualTo(1L);
            verify(loadQuestionPort).existsQuestionById(questionId);

            ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);
            verify(saveAnswerPort).save(captor.capture());

            Answer captorAnswer = captor.getValue();
            assertThat(captorAnswer.getContent()).isEqualTo(content);
            assertThat(captorAnswer.getAuthorId()).isEqualTo(authorId);
            assertThat(captorAnswer.getQuestionId()).isEqualTo(questionId);
        }

        @Test
        @DisplayName("존재하지 않는 질문에 답변을 생성하면 QuestionNotFoundException이 발생한다")
        void createAnswer_질문없음_예외() {
            // given
            String content = "test content";
            Long questionId = 999L;
            Long authorId = 100L;
            AnswerCreateDto createDto = AnswerCreateDto.of(content, questionId, authorId);

            when(loadQuestionPort.existsQuestionById(questionId)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> answerFacade.createAnswer(createDto))
                    .isInstanceOf(QuestionNotFoundException.class);

            verify(loadQuestionPort).existsQuestionById(questionId);
            verify(saveAnswerPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("답변 목록 조회 테스트")
    class GetAllAnswerTests {

        @Test
        @DisplayName("질문의 답변 목록을 정상적으로 조회한다")
        void getAllAnswer_성공() {
            // given
            Long questionId = 1L;
            Pageable pageable = PageRequest.of(0, 10);

            Long answerId = 1L;
            Long authorId = 100L;
            String content = "test content";
            String authorEmail = "user@example.com";
            AnswerDetailDto answerDto = new AnswerDetailDto(answerId, content, authorId, authorEmail);
            Page<AnswerDetailDto> answerPage = new PageImpl<>(List.of(answerDto));

            when(loadQuestionPort.existsQuestionById(questionId)).thenReturn(true);
            when(loadAnswerPort.findAllByQuestionId(questionId, pageable)).thenReturn(answerPage);

            // when
            Page<AnswerDetailDto> result = answerFacade.getAllAnswerByQuestionId(questionId, pageable);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            
            AnswerDetailDto firstAnswer = result.getContent().get(0);
            assertThat(firstAnswer.id()).isEqualTo(answerId);
            assertThat(firstAnswer.content()).isEqualTo(content);
            assertThat(firstAnswer.authorId()).isEqualTo(authorId);
            assertThat(firstAnswer.authorEmail()).isEqualTo(authorEmail);
            
            verify(loadQuestionPort).existsQuestionById(questionId);
            verify(loadAnswerPort).findAllByQuestionId(questionId, pageable);
        }

        @Test
        @DisplayName("질문이 존재하지만 답변이 없으면 빈 페이지를 반환한다")
        void getAllAnswer_질문있음_답변없음_빈페이지() {
            // given
            Long questionId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<AnswerDetailDto> emptyPage = new PageImpl<>(List.of());

            when(loadQuestionPort.existsQuestionById(questionId)).thenReturn(true);
            when(loadAnswerPort.findAllByQuestionId(questionId, pageable)).thenReturn(emptyPage);

            // when
            Page<AnswerDetailDto> result = answerFacade.getAllAnswerByQuestionId(questionId, pageable);

            // then
            assertThat(result).isEmpty();
            verify(loadQuestionPort).existsQuestionById(questionId);
            verify(loadAnswerPort).findAllByQuestionId(questionId, pageable);
        }

        @Test
        @DisplayName("존재하지 않는 질문의 답변을 조회하면 QuestionNotFoundException이 발생한다")
        void getAllAnswer_질문없음_예외() {
            // given
            Long questionId = 999L;
            Pageable pageable = PageRequest.of(0, 10);

            when(loadQuestionPort.existsQuestionById(questionId)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> answerFacade.getAllAnswerByQuestionId(questionId, pageable))
                    .isInstanceOf(QuestionNotFoundException.class);

            verify(loadQuestionPort).existsQuestionById(questionId);
            verify(loadAnswerPort, never()).findAllByQuestionId(any(), any());
        }
    }

    @Nested
    @DisplayName("답변 수정 테스트")
    class UpdateAnswerTests {

        @Test
        @DisplayName("정상적으로 답변을 수정한다")
        void updateAnswer_성공() {
            // given
            Long answerId = 1L;
            Long authorId = 100L;
            String originalContent = "원래 내용";
            String updatedContent = "수정된 내용";

            Answer answer = Answer.of(originalContent, 1L, authorId);
            AnswerUpdateDto updateDto = new AnswerUpdateDto(answerId, updatedContent, authorId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.of(answer));
            doNothing().when(ownershipValidator).validateAuthor(
                    eq(authorId), eq(authorId), any()
            );

            // when
            answerFacade.updateAnswer(updateDto);

            // then
            assertThat(answer.getContent()).isEqualTo(updatedContent);
            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator).validateAuthor(eq(authorId), eq(authorId), any());
        }

        @Test
        @DisplayName("내용 변경이 없으면 update 메서드를 호출하지 않는다")
        void updateAnswer_변경없음() {
            // given
            Long answerId = 1L;
            Long authorId = 100L;
            String content = "test content";

            Answer answer = spy(Answer.of(content, 1L, authorId));
            AnswerUpdateDto updateDto = new AnswerUpdateDto(answerId, content, authorId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.of(answer));
            doNothing().when(ownershipValidator).validateAuthor(
                    eq(authorId), eq(authorId), any()
            );

            // when
            answerFacade.updateAnswer(updateDto);

            // then
            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator).validateAuthor(eq(authorId), eq(authorId), any());
            verify(answer, never()).update(anyString());
        }

        @Test
        @DisplayName("존재하지 않는 답변을 수정하면 AnswerNotFoundException이 발생한다")
        void updateAnswer_답변없음_예외() {
            // given
            Long answerId = 999L;
            Long authorId = 100L;
            String content = "test content";
            AnswerUpdateDto updateDto = new AnswerUpdateDto(answerId, content, authorId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> answerFacade.updateAnswer(updateDto))
                    .isInstanceOf(AnswerNotFoundException.class);

            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator, never()).validateAuthor(any(), any(), any());
        }

        @Test
        @DisplayName("다른 사용자의 답변을 수정하면 AnswerUpdateForbiddenException이 발생한다")
        void updateAnswer_권한없음_예외() {
            // given
            Long answerId = 1L;
            Long authorId = 100L;
            Long otherUserId = 200L;
            String content = "test content";

            Answer answer = Answer.of(content, 1L, authorId);
            AnswerUpdateDto updateDto = new AnswerUpdateDto(answerId, "수정 내용", otherUserId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.of(answer));
            doThrow(new AnswerUpdateForbiddenException())
                    .when(ownershipValidator).validateAuthor(
                            eq(authorId), eq(otherUserId), any()
                    );

            // when & then
            assertThatThrownBy(() -> answerFacade.updateAnswer(updateDto))
                    .isInstanceOf(AnswerUpdateForbiddenException.class);

            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator).validateAuthor(eq(authorId), eq(otherUserId), any());
        }
    }

    @Nested
    @DisplayName("답변 삭제 테스트")
    class DeleteAnswerTests {

        @Test
        @DisplayName("정상적으로 답변을 삭제한다")
        void deleteAnswer_성공() {
            // given
            Long answerId = 1L;
            Long authorId = 100L;
            String content = "test content";


            Answer answer = Answer.of(content, 1L, authorId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.of(answer));
            doNothing().when(ownershipValidator).validateAuthor(
                    eq(authorId), eq(authorId), any()
            );
            doNothing().when(deleteAnswerPort).deleteById(answerId);

            // when
            answerFacade.deleteAnswer(answerId, authorId);

            // then
            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator).validateAuthor(eq(authorId), eq(authorId), any());
            verify(deleteAnswerPort).deleteById(answerId);
        }

        @Test
        @DisplayName("존재하지 않는 답변을 삭제하면 AnswerNotFoundForDeleteException이 발생한다")
        void deleteAnswer_답변없음_예외() {
            // given
            Long answerId = 999L;
            Long userId = 100L;

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> answerFacade.deleteAnswer(answerId, userId))
                    .isInstanceOf(AnswerNotFoundForDeleteException.class);

            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator, never()).validateAuthor(any(), any(), any());
            verify(deleteAnswerPort, never()).deleteById(any());
        }

        @Test
        @DisplayName("다른 사용자의 답변을 삭제하면 QuestionDeleteForbiddenException이 발생한다")
        void deleteAnswer_권한없음_예외() {
            // given
            Long answerId = 1L;
            Long authorId = 100L;
            Long otherUserId = 200L;

            Answer answer = Answer.of("내용", 1L, authorId);

            when(loadAnswerPort.findById(answerId)).thenReturn(Optional.of(answer));
            doThrow(new QuestionDeleteForbiddenException())
                    .when(ownershipValidator).validateAuthor(
                            eq(authorId), eq(otherUserId), any()
                    );

            // when & then
            assertThatThrownBy(() -> answerFacade.deleteAnswer(answerId, otherUserId))
                    .isInstanceOf(QuestionDeleteForbiddenException.class);

            verify(loadAnswerPort).findById(answerId);
            verify(ownershipValidator).validateAuthor(eq(authorId), eq(otherUserId), any());
            verify(deleteAnswerPort, never()).deleteById(any());
        }
    }
}

