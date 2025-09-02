package ajaajaja.debugging_rounge.feature.question.application.dto;

public record QuestionUpdateDto(
        Long id,
        String title,
        String content,
        Long loginUserId
) {
    public static QuestionUpdateDto of(Long id, String title, String content, Long loginUserId) {
        return new QuestionUpdateDto(id, title, content, loginUserId);
    }
}
