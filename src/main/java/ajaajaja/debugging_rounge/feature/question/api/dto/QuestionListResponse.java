package ajaajaja.debugging_rounge.feature.question.api.dto;

public record QuestionListResponse(
        Long questionId,
        String title,
        String previewContent,
        String authorEmail,
        Integer recommendScore) {
}
