package ajaajaja.debugging_rounge.feature.answer.recommend.api.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

public record AnswerRecommendUpdateRequest(
        RecommendType recommendType
) {
}
