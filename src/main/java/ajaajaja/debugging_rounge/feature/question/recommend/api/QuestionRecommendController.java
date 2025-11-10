package ajaajaja.debugging_rounge.feature.question.recommend.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.recommend.api.mapper.QuestionRecommendMapper;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.in.UpdateQuestionRecommendUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class QuestionRecommendController {

    private final UpdateQuestionRecommendUseCase updateQuestionRecommendUseCase;
    private final QuestionRecommendMapper questionRecommendMapper;

    @PatchMapping("/questions/{questionId}/recommend")
    public ResponseEntity<QuestionRecommendScoreAndMyRecommendTypeResponse> updateQuestionRecommend(
            @PathVariable("questionId") Long questionId,
            @RequestBody QuestionRecommendUpdateRequest questionRecommendUpdateRequest,
            @LoginUserId Long loginUserId) {
        QuestionRecommendScoreAndMyRecommendTypeDto dto = updateQuestionRecommendUseCase.update(
                questionRecommendMapper.toDto(questionRecommendUpdateRequest, questionId, loginUserId));

        return ResponseEntity.ok(questionRecommendMapper.toResponse(dto));
    }
}
