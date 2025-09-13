package ajaajaja.debugging_rounge.feature.answer.api.dto;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerCreateRequest(
        @NotBlank(message = "error.answer.content.required")
        @Size(min = 10, message = "error.answer.content.size")
        String content
) {
    public AnswerCreateDto toDto(Long questionId, Long userId) {
        return AnswerCreateDto.of(content, questionId, userId);
    }
}
