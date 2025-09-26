package ajaajaja.debugging_rounge.feature.answer.api.dto;

public record AnswerDetailResponse(
        Long id,
        String content,
        Long userId,
        Long loginUserId
) {
}
