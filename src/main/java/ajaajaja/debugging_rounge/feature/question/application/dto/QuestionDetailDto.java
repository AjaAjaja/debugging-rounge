package ajaajaja.debugging_rounge.feature.question.application.dto;

public record QuestionDetailDto(
        Long questionId,
        String title,
        String content,
        Long authorId,
        String authorEmail
) {
}
