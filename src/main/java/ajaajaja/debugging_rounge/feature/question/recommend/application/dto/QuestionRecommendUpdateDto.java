package ajaajaja.debugging_rounge.feature.question.recommend.application.dto;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;

public record QuestionRecommendUpdateDto(
        Long questionId,
        RecommendType recommendType,
        Long userId
) {
}
