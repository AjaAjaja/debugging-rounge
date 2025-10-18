package ajaajaja.debugging_rounge.feature.question.recommend.application.dto;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;

public record QuestionRecommendScoreAndMyRecommendTypeDto(
        Integer recommendScore,
        RecommendType myRecommendType
) {
    public static QuestionRecommendScoreAndMyRecommendTypeDto of(Integer recommendScore, RecommendType recommendType) {
        return new QuestionRecommendScoreAndMyRecommendTypeDto(recommendScore, recommendType);
    }
}
