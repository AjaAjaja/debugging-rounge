package ajaajaja.debugging_rounge.feature.question.application.dto;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailWithRecommendDto;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import org.springframework.data.domain.Page;

public record QuestionWithAnswersDto(
        Long questionId,
        String title,
        String content,
        Long authorId,
        String authorEmail,
        RecommendType myRecommendType,
        Integer recommendScore,
        Page<AnswerDetailWithRecommendDto> answers
) {}
