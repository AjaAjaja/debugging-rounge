package ajaajaja.debugging_rounge.feature.question.recommend.api.dto;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;

public record QuestionRecommendScoreAndMyRecommendTypeResponse(
        Integer recommendScore,
        RecommendType myRecommendType
) {
}
