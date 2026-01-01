package ajaajaja.debugging_rounge.feature.answer.api.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

import java.util.List;

public record AnswerDetailResponse(
        Long id,
        String content,
        Long authorId,
        String authorEmail,
        Boolean mine,
        RecommendType myRecommendType,
        Integer recommendScore,
        List<String> imageUrls
) {
}
