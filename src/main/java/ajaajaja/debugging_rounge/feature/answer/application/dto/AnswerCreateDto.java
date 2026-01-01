package ajaajaja.debugging_rounge.feature.answer.application.dto;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;

import java.util.List;

public record AnswerCreateDto(
        String content,
        Long questionId,
        Long userId,
        List<String> imageUrls
) {
    public Answer toEntity() {
        return Answer.of(content, questionId, userId);
    }

    public static AnswerCreateDto of(String content, Long questionId, Long userId, List<String> imageUrls) {
        return new AnswerCreateDto(content, questionId, userId, imageUrls);
    }
}
