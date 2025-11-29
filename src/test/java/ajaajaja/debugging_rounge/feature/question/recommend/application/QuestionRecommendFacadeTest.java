package ajaajaja.debugging_rounge.feature.question.recommend.application;

import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendScorePort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.UpsertQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionRecommendFacadeTest {

    @Mock
    private LoadQuestionRecommendScorePort loadQuestionRecommendScorePort;

    @Mock
    private DeleteQuestionRecommendPort deleteQuestionRecommendPort;

    @Mock
    private UpsertQuestionRecommendPort upsertQuestionRecommendPort;

    private QuestionRecommendFacade questionRecommendFacade;

    @BeforeEach
    void setup() {
        questionRecommendFacade = new QuestionRecommendFacade(
                loadQuestionRecommendScorePort,
                deleteQuestionRecommendPort,
                upsertQuestionRecommendPort);
    }

    @Test
    @DisplayName("NONE 요청이면 기존 기록을 삭제하고 점수를 조회한다")
    void NONE요청이면_기존기록을삭제하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.NONE,
                userId
        );

        when(loadQuestionRecommendScorePort.findRecommendScoreByQuestionId(questionId)).thenReturn(0);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.update(requestDto);

        // then
        // 삭제 포트가 호출이 잘 되었나?
        verify(deleteQuestionRecommendPort).deleteByQuestionIdAndUserId(questionId, userId);

        // 혹시 업서트 포트가 호출되지 않았나?
        verify(upsertQuestionRecommendPort, never()).upsert(anyLong(), anyLong(), anyString());

        assertThat(result.recommendScore()).isEqualTo(0);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.NONE);
    }

    @Test
    @DisplayName("UP 요청이면 업서트하고 점수를 조회한다")
    void UP요청이면_업서트하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.UP,
                userId
        );

        when(loadQuestionRecommendScorePort.findRecommendScoreByQuestionId(questionId)).thenReturn(2);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.update(requestDto);

        // then
        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertQuestionRecommendPort)
                .upsert(questionId, userId, RecommendType.UP.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteQuestionRecommendPort, never())
                .deleteByQuestionIdAndUserId(anyLong(), anyLong());

        assertThat(result.recommendScore()).isEqualTo(2);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.UP);
    }

    @Test
    @DisplayName("DOWN 요청이면 업서트하고 점수를 조회한다")
    void DOWN요청이면_업서트하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.DOWN,
                userId
        );

        when(loadQuestionRecommendScorePort.findRecommendScoreByQuestionId(questionId)).thenReturn(-10);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.update(requestDto);

        // then
        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertQuestionRecommendPort)
                .upsert(questionId, userId, RecommendType.DOWN.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteQuestionRecommendPort, never())
                .deleteByQuestionIdAndUserId(anyLong(), anyLong());

        assertThat(result.recommendScore()).isEqualTo(-10);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.DOWN);
    }

}