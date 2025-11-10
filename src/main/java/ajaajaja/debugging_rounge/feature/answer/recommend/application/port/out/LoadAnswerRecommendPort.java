package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;

import java.util.List;

public interface LoadAnswerRecommendPort {
    List<AnswerRecommendScoreAndMyRecommendTypeDto> findRecommendScoreAndMyType(List<Long> answerIds, Long userId);

    Integer findRecommendScoreByAnswerId(Long answerId);
}
