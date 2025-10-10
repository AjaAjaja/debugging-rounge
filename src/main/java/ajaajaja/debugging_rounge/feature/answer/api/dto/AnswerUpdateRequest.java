package ajaajaja.debugging_rounge.feature.answer.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerUpdateRequest(
        @NotBlank(message = "error.answer.content.required")
        @Size(min = 5, max = 10000, message = "error.answer.content.size")
        String content
) {
}
