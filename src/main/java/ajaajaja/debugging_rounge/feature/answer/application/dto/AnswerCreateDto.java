package ajaajaja.debugging_rounge.feature.answer.application.dto;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;

public record AnswerCreateDto(
        String content,
        Long questionId,
        Long userId
) {
    public Answer toEntity() {
        return Answer.of(content, questionId, userId);
    }

    public static AnswerCreateDto of(String content, Long questionId, Long userId) {
        return new AnswerCreateDto(content, questionId, userId);
    }
}
