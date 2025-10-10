package ajaajaja.debugging_rounge.feature.answer.application.dto;

public record AnswerUpdateDto(
        Long id,
        String content,
        Long authorId
) {
}
