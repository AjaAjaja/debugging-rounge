package ajaajaja.debugging_rounge.feature.question.recommend.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.recommend.api.mapper.QuestionRecommendMapper;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.in.UpdateQuestionRecommendUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Question Recommend", description = "질문 추천 관련 API")
@RestController
@RequiredArgsConstructor
public class QuestionRecommendController {

    private final UpdateQuestionRecommendUseCase updateQuestionRecommendUseCase;
    private final QuestionRecommendMapper questionRecommendMapper;

    @Operation(
            summary = "질문 추천 업데이트",
            description = "질문에 대한 추천(좋아요/싫어요)을 생성하거나 업데이트합니다.\n\n" +
                    "**요청 예시:** `{\"recommendType\": \"UP\"}`\n\n" +
                    "**추천 타입:** `UP` (좋아요), `DOWN` (싫어요), `NONE` (추천 취소)",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 업데이트 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/questions/{questionId}/recommend")
    public ResponseEntity<QuestionRecommendScoreAndMyRecommendTypeResponse> updateQuestionRecommend(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @RequestBody QuestionRecommendUpdateRequest questionRecommendUpdateRequest,
            @Parameter(hidden = true) @LoginUserId Long loginUserId) {
        QuestionRecommendScoreAndMyRecommendTypeDto dto = updateQuestionRecommendUseCase.update(
                questionRecommendMapper.toDto(questionRecommendUpdateRequest, questionId, loginUserId));

        return ResponseEntity.ok(questionRecommendMapper.toResponse(dto));
    }
}
