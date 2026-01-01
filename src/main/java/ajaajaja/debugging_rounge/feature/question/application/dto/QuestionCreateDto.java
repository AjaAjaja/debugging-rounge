package ajaajaja.debugging_rounge.feature.question.application.dto;

import ajaajaja.debugging_rounge.feature.question.domain.Question;

import java.util.List;

public record QuestionCreateDto(
        String title,
        String content,
        Long authorId,
        List<String> imageUrls
) {
    public static QuestionCreateDto of(String title, String content, Long authorId, List<String> imageUrls) {
        return new QuestionCreateDto(title, content, authorId, imageUrls);
    }

    public Question toEntity() {
        return Question.of(title, content, authorId);
    }
}
