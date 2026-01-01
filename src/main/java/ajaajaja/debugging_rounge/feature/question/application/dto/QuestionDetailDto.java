package ajaajaja.debugging_rounge.feature.question.application.dto;

import java.util.List;

public record QuestionDetailDto(
        Long questionId,
        String title,
        String content,
        Long authorId,
        String authorEmail,
        Integer recommendScore,
        List<String> imageUrls
) {
}
