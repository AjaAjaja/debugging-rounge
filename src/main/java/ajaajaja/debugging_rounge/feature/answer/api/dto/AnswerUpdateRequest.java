package ajaajaja.debugging_rounge.feature.answer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "답변 수정 요청")
public record AnswerUpdateRequest(
        @Schema(
                description = "답변 내용 (필수, 5자 이상 10,000자 이하)",
                example = "양방향 매핑 시 주의사항은 다음과 같습니다. 1. 연관관계의 주인을 명확히 설정해야 합니다.2. 순환 참조를 방지하기 위해 한쪽은 @JsonIgnore를 사용하는 것이 좋습니다.3. 양쪽 모두 값을 설정하면 무한 루프가 발생할 수 있습니다.",
                required = true,
                minLength = 5,
                maxLength = 10000
        )
        @NotBlank(message = "error.answer.content.required")
        @Size(min = 5, max = 10000, message = "error.answer.content.size")
        String content
) {
}
