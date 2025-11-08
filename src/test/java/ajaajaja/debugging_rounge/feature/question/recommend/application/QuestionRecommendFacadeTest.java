package ajaajaja.debugging_rounge.feature.question.recommend.application;

import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendScorePort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.UpsertQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import org.junit.jupiter.api.BeforeEach;
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
    void NONE요청이면_기존기록을삭제하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.NONE,
                userId
        );

        when(loadQuestionRecommendScorePort.getQuestionRecommendScoreByQuestionId(questionId)).thenReturn(0);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.UpdateQuestionRecommend(requestDto);

        // then
        // 삭제 포트가 호출이 잘 되었나?
        verify(deleteQuestionRecommendPort).deleteQuestionRecommendByQuestionIdAndUserId(questionId, userId);

        // 혹시 업서트 포트가 호출되지 않았나?
        verify(upsertQuestionRecommendPort, never()).insertOrUpdateQuestionRecommend(anyLong(), anyLong(), anyString());

        // then
        assertThat(result.recommendScore()).isEqualTo(0);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.NONE);
    }

    @Test
    void UP요청이면_업서트하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.UP,
                userId
        );

        when(loadQuestionRecommendScorePort.getQuestionRecommendScoreByQuestionId(questionId)).thenReturn(2);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.UpdateQuestionRecommend(requestDto);

        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertQuestionRecommendPort)
                .insertOrUpdateQuestionRecommend(questionId, userId, RecommendType.UP.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteQuestionRecommendPort, never())
                .deleteQuestionRecommendByQuestionIdAndUserId(anyLong(), anyLong());

        // then
        assertThat(result.recommendScore()).isEqualTo(2);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.UP);
    }

    @Test
    void DOWN요청이면_업서트하고_점수를조회한다() {

        // given
        Long questionId = 1L;
        Long userId = 10L;

        QuestionRecommendUpdateDto requestDto = new QuestionRecommendUpdateDto(
                questionId,
                RecommendType.DOWN,
                userId
        );

        when(loadQuestionRecommendScorePort.getQuestionRecommendScoreByQuestionId(questionId)).thenReturn(5);

        // when
        QuestionRecommendScoreAndMyRecommendTypeDto result =
                questionRecommendFacade.UpdateQuestionRecommend(requestDto);

        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertQuestionRecommendPort)
                .insertOrUpdateQuestionRecommend(questionId, userId, RecommendType.DOWN.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteQuestionRecommendPort, never())
                .deleteQuestionRecommendByQuestionIdAndUserId(anyLong(), anyLong());

        // then
        assertThat(result.recommendScore()).isEqualTo(5);
        assertThat(result.myRecommendType()).isEqualTo(RecommendType.DOWN);
    }

}