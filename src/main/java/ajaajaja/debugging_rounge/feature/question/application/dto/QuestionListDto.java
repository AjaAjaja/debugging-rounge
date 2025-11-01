package ajaajaja.debugging_rounge.feature.question.application.dto;

public record QuestionListDto(
        Long questionId,
        String title,
        String previewContent,
        String authorEmail,
        Integer recommendScore
) {
}
