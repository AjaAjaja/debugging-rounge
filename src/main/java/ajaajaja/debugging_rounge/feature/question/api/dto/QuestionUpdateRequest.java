package ajaajaja.debugging_rounge.feature.question.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "질문 수정 요청")
public record QuestionUpdateRequest(
        @Schema(
                description = "질문 제목 (필수, 최대 50자)",
                example = "Spring Boot에서 JPA 연관관계 매핑 방법 (수정)",
                required = true,
                maxLength = 50
        )
        @NotBlank(message = "error.question.title.required")
        @Size(max = 50, message = "error.question.title.size")
        String title,
        @Schema(
                description = "질문 내용 (필수, 최대 10,000자)",
                example = "Spring Boot에서 @OneToMany와 @ManyToOne을 사용하여 연관관계를 매핑하려고 하는데, 양방향 매핑 시 주의사항이 있을까요? 추가로 Lazy Loading에 대해서도 궁금합니다.",
                required = true,
                maxLength = 10000
        )
        @NotBlank(message = "error.question.content.required")
        @Size(max = 10000, message = "error.question.content.size")
        String content
) {
}