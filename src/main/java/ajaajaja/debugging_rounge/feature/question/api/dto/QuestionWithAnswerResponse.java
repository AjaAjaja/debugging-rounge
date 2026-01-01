package ajaajaja.debugging_rounge.feature.question.api.dto;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import org.springframework.data.domain.Page;

import java.util.List;

public record QuestionWithAnswerResponse(
        Long questionId,
        String title,
        String content,
        String authorEmail,
        Boolean mine,
        RecommendType myRecommendType,
        Integer recommendScore,
        List<String> imageUrls,
        Page<AnswerDetailResponse> answers
) {
}
