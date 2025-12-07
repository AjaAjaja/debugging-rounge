package ajaajaja.debugging_rounge.feature.answer.recommend.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.mapper.AnswerRecommendMapper;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.in.UpdateAnswerRecommendUseCase;
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

@Tag(name = "Answer Recommend", description = "답변 추천 관련 API")
@RestController
@RequiredArgsConstructor
public class AnswerRecommendController {

    private final UpdateAnswerRecommendUseCase updateAnswerRecommendUseCase;
    private final AnswerRecommendMapper answerRecommendMapper;

    @Operation(
            summary = "답변 추천 업데이트",
            description = "답변에 대한 추천(좋아요/싫어요)을 생성하거나 업데이트합니다.\n\n" +
                    "**요청 예시:** `{\"recommendType\": \"UP\"}`\n\n" +
                    "**추천 타입:** `UP` (좋아요), `DOWN` (싫어요), `NONE` (추천 취소)",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 업데이트 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/answers/{answerId}/recommend")
    public ResponseEntity<AnswerRecommendScoreAndMyRecommendTypeResponse> updateAnswerRecommend(
            @Parameter(description = "답변 ID", required = true, example = "1") @PathVariable("answerId") Long answerId,
            @RequestBody AnswerRecommendUpdateRequest answerRecommendUpdateRequest,
            @Parameter(hidden = true) @LoginUserId Long loginUserId
            ) {
        AnswerRecommendScoreAndMyRecommendTypeDto answerRecommendScoreAndMyRecommendTypeDto = updateAnswerRecommendUseCase.update(
                answerRecommendMapper.toDto(answerRecommendUpdateRequest, answerId, loginUserId));

        return ResponseEntity.ok(answerRecommendMapper.toResponse(answerRecommendScoreAndMyRecommendTypeDto));
    }
}
