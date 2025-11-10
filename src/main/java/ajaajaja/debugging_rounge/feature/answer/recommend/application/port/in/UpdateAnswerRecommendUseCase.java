package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.in;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendUpdateDto;

public interface UpdateAnswerRecommendUseCase {
    AnswerRecommendScoreAndMyRecommendTypeDto update(AnswerRecommendUpdateDto answerRecommendUpdateDto);
}
