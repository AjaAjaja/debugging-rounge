package ajaajaja.debugging_rounge.feature.answer.recommend.application;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.DeleteAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.LoadAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.UpsertAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerRecommendFacadeTest {

    @Mock
    private LoadAnswerRecommendPort loadAnswerRecommendPort;

    @Mock
    private UpsertAnswerRecommendPort upsertAnswerRecommendPort;

    @Mock
    private DeleteAnswerRecommendPort deleteAnswerRecommendPort;

    private AnswerRecommendFacade answerRecommendFacade;

    @BeforeEach
    void setup() {
        answerRecommendFacade = new AnswerRecommendFacade(
                loadAnswerRecommendPort,
                upsertAnswerRecommendPort,
                deleteAnswerRecommendPort
        );
    }

    @Test
    @DisplayName("NONE 요청이면 기존 기록을 삭제하고 점수를 조회한다")
    void NONE요청이면_기존기록을삭제하고_점수를_조회() {
        // given
        Long answerId = 1L;
        Long userId = 10L;

        AnswerRecommendUpdateDto requestDto = new AnswerRecommendUpdateDto(
                answerId,
                RecommendType.NONE,
                userId
        );

        when(loadAnswerRecommendPort.findRecommendScoreByAnswerId(answerId)).thenReturn(0);

        // when
        AnswerRecommendScoreAndMyRecommendTypeDto result = answerRecommendFacade.update(requestDto);

        // then
        // 삭제 포트가 호출이 잘 되었나?
        verify(deleteAnswerRecommendPort).deleteByAnswerIdAndUserId(answerId, userId);

        // 혹시 업서트 포트가 호출되지 않았나?
        verify(upsertAnswerRecommendPort, never()).upsert(anyLong(), anyLong(), anyString());

        assertThat(result.answerId()).isEqualTo(answerId);
        assertThat(result.answerRecommendScore()).isEqualTo(0);
        assertThat(result.myAnswerRecommendType()).isEqualTo(RecommendType.NONE);
    }

    @Test
    @DisplayName("UP 요청이면 업서트하고 점수를 조회한다")
    void UP요청이면_업서트하고_점수를_조회() {
        // given
        Long answerId = 1L;
        Long userId = 10L;

        AnswerRecommendUpdateDto requestDto = new AnswerRecommendUpdateDto(
                answerId,
                RecommendType.UP,
                userId
        );

        when(loadAnswerRecommendPort.findRecommendScoreByAnswerId(answerId)).thenReturn(2);

        // when
        AnswerRecommendScoreAndMyRecommendTypeDto result = answerRecommendFacade.update(requestDto);

        // then
        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertAnswerRecommendPort)
                .upsert(answerId, userId, RecommendType.UP.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteAnswerRecommendPort, never())
                .deleteByAnswerIdAndUserId(anyLong(), anyLong());

        assertThat(result.answerId()).isEqualTo(answerId);
        assertThat(result.answerRecommendScore()).isEqualTo(2);
        assertThat(result.myAnswerRecommendType()).isEqualTo(RecommendType.UP);
    }

    @Test
    @DisplayName("DOWN 요청이면 업서트하고 점수를 조회한다")
    void DOWN요청이면_업서트하고_점수를_조회() {
        // given
        Long answerId = 1L;
        Long userId = 10L;

        AnswerRecommendUpdateDto requestDto = new AnswerRecommendUpdateDto(
                answerId,
                RecommendType.DOWN,
                userId
        );

        when(loadAnswerRecommendPort.findRecommendScoreByAnswerId(answerId)).thenReturn(-10);

        // when
        AnswerRecommendScoreAndMyRecommendTypeDto result = answerRecommendFacade.update(requestDto);

        // then
        // 업서트 포트가 호출이 잘 되었나?
        verify(upsertAnswerRecommendPort)
                .upsert(answerId, userId, RecommendType.DOWN.name());

        // 혹시 삭제 포트가 호출되지 않았나?
        verify(deleteAnswerRecommendPort, never())
                .deleteByAnswerIdAndUserId(anyLong(), anyLong());

        assertThat(result.answerId()).isEqualTo(answerId);
        assertThat(result.answerRecommendScore()).isEqualTo(-10);
        assertThat(result.myAnswerRecommendType()).isEqualTo(RecommendType.DOWN);
    }
}

