package ajaajaja.debugging_rounge.feature.answer.recommend.application.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

public record AnswerRecommendUpdateDto(
        Long answerId,
        RecommendType recommendType,
        Long userId
) {}
