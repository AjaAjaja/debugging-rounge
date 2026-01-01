package ajaajaja.debugging_rounge.feature.answer.application.dto;

import java.util.List;

public record AnswerDetailDto(
        Long id,
        String content,
        Long authorId,
        String authorEmail,
        List<String> imageUrls
) {
}
