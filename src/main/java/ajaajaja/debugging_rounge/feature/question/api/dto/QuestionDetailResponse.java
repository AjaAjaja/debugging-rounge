package ajaajaja.debugging_rounge.feature.question.api.dto;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import org.springframework.data.domain.Page;

public record QuestionDetailResponse(
        Long questionId,
        String title,
        String content,
        String authorEmail,
        Boolean mine,
        Page<AnswerDetailResponse> answers
) {
}
