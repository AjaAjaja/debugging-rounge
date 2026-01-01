package ajaajaja.debugging_rounge.feature.question.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "질문 생성 요청")
public record QuestionCreateRequest(
        @Schema(
                description = "질문 제목 (필수, 최대 50자)",
                example = "Spring Boot에서 JPA 연관관계 매핑 방법",
                required = true,
                maxLength = 50
        )
        @NotBlank(message = "error.question.title.required")
        @Size(max = 50, message = "error.question.title.size")
        String title,
        @Schema(
                description = "질문 내용 (필수, 최대 10,000자)",
                example = "Spring Boot에서 @OneToMany와 @ManyToOne을 사용하여 연관관계를 매핑하려고 하는데, 양방향 매핑 시 주의사항이 있을까요?",
                required = true,
                maxLength = 10000
        )
        @NotBlank(message = "error.question.content.required")
        @Size(max = 10000, message = "error.question.content.size")
        String content,
        @Schema(
                description = "이미지 URL 목록 (선택)",
                example = "[\"https://example.com/images/uuid1.jpg\", \"https://example.com/images/uuid2.jpg\"]"
        )
        List<String> imageUrls
) {
}
