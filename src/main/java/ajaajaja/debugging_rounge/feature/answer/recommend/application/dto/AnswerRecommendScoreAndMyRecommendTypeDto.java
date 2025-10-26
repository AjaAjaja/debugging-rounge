package ajaajaja.debugging_rounge.feature.answer.recommend.application.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

public record AnswerRecommendScoreAndMyRecommendTypeDto(

        Long answerId,
        Integer answerRecommendScore,
        RecommendType myAnswerRecommendType
) {
    public static AnswerRecommendScoreAndMyRecommendTypeDto of(Long answerId, Integer answerRecommendScore, RecommendType recommendType) {
        return new AnswerRecommendScoreAndMyRecommendTypeDto(answerId, answerRecommendScore, recommendType);
    }
}
