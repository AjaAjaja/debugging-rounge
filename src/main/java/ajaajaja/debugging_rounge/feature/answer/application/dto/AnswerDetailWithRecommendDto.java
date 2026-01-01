package ajaajaja.debugging_rounge.feature.answer.application.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

import java.util.List;

public record AnswerDetailWithRecommendDto(
        Long id,
        String content,
        Long authorId,
        String authorEmail,
        RecommendType myRecommendType,
        Integer recommendScore,
        List<String> imageUrls
) {
    public static AnswerDetailWithRecommendDto of(
            AnswerDetailDto detailDto, AnswerRecommendScoreAndMyRecommendTypeDto scoreAndMyRecommendTypeDto) {

        RecommendType myAnswerRecommendType =
                scoreAndMyRecommendTypeDto != null ? scoreAndMyRecommendTypeDto.myAnswerRecommendType() : RecommendType.NONE;
        Integer answerRecommendScore =
                scoreAndMyRecommendTypeDto != null ? scoreAndMyRecommendTypeDto.answerRecommendScore() : 0;

        return new AnswerDetailWithRecommendDto(
                detailDto.id(),
                detailDto.content(),
                detailDto.authorId(),
                detailDto.authorEmail(),
                myAnswerRecommendType,
                answerRecommendScore,
                detailDto.imageUrls()
        );
    }
}
