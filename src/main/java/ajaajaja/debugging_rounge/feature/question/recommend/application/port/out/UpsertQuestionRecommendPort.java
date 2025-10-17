package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;

public interface UpsertQuestionRecommendPort {
    void insertOrUpdateQuestionRecommend(Long questionId, Long userId, RecommendType type);
}
