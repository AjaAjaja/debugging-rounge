package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;

import java.util.List;

public interface LoadAnswerRecommendPort {
    List<AnswerRecommendScoreAndMyRecommendTypeDto> getAnswerRecommendScoreAndMyType(List<Long> answerIds, Long userId);

    Integer getAnswerRecommendScoreByAnswerId(Long answerId);
}
