package ajaajaja.debugging_rounge.feature.question.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionUpdateRequest(
        @NotBlank(message = "error.question.title.required")
        @Size(max = 50, message = "error.question.title.size")
        String title,
        @NotBlank(message = "error.question.content.required")
        @Size(max = 10000, message = "error.question.content.size")
        String content
) {
}