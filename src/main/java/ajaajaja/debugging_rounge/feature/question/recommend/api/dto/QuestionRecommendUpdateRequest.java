package ajaajaja.debugging_rounge.feature.question.recommend.api.dto;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;

public record QuestionRecommendUpdateRequest(
        RecommendType recommendType
) {
}
