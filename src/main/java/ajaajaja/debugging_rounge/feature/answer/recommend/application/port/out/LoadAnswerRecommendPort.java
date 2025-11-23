package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;

import java.util.List;
import java.util.Optional;

public interface LoadAnswerRecommendPort {
    List<AnswerRecommendScoreAndMyRecommendTypeDto> findRecommendScoreAndMyType(List<Long> answerIds, Long userId);

    Integer findRecommendScoreByAnswerId(Long answerId);

    Optional<AnswerRecommend> findByAnswerIdAndUserID(Long answerId, Long userId);
}
