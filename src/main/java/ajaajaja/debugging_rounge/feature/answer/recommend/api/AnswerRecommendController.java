package ajaajaja.debugging_rounge.feature.answer.recommend.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.mapper.AnswerRecommendMapper;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.in.UpdateAnswerRecommendUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AnswerRecommendController {

    private final UpdateAnswerRecommendUseCase updateAnswerRecommendUseCase;
    private final AnswerRecommendMapper answerRecommendMapper;

    @PatchMapping("/answers/{answerId}/recommend")
    public ResponseEntity<AnswerRecommendScoreAndMyRecommendTypeResponse> updateAnswerRecommend(
            @PathVariable("answerId") Long answerId,
            @RequestBody AnswerRecommendUpdateRequest answerRecommendUpdateRequest,
            @LoginUserId Long loginUserId
            ) {
        AnswerRecommendScoreAndMyRecommendTypeDto answerRecommendScoreAndMyRecommendTypeDto = updateAnswerRecommendUseCase.updateAnswerRecommend(
                answerRecommendMapper.toDto(answerRecommendUpdateRequest, answerId, loginUserId));

        return ResponseEntity.ok(answerRecommendMapper.toResponse(answerRecommendScoreAndMyRecommendTypeDto));
    }
}
