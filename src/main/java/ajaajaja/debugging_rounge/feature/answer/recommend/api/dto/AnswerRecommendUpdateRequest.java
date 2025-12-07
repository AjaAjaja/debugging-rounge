package ajaajaja.debugging_rounge.feature.answer.recommend.api.dto;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "답변 추천 업데이트 요청",
        example = "{\"recommendType\": \"UP\"}"
)
public record AnswerRecommendUpdateRequest(
        @Schema(
                description = "추천 타입\n" +
                        "- `UP`: 좋아요 (추천)\n" +
                        "- `DOWN`: 싫어요 (비추천)\n" +
                        "- `NONE`: 추천 취소",
                example = "UP",
                allowableValues = {"UP", "DOWN", "NONE"}
        )
        RecommendType recommendType
) {
}
