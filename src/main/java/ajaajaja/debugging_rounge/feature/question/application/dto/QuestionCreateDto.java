package ajaajaja.debugging_rounge.feature.question.application.dto;

import ajaajaja.debugging_rounge.feature.question.domain.Question;

public record QuestionCreateDto(
        String title,
        String content,
        Long authorId
) {
    public static QuestionCreateDto of(String title, String content, Long authorId) {
        return new QuestionCreateDto(title, content, authorId);
    }

    public Question toEntity() {
        return Question.of(title, content, authorId);
    }
}
