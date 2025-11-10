package ajaajaja.debugging_rounge.feature.answer.recommend.application;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.in.UpdateAnswerRecommendUseCase;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.*;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerRecommendFacade implements UpdateAnswerRecommendUseCase {

    private final LoadAnswerRecommendPort loadAnswerRecommendPort;
    private final UpsertAnswerRecommendPort upsertAnswerRecommendPort;
    private final DeleteAnswerRecommendPort deleteAnswerRecommendPort;


    @Override
    @Transactional
    public AnswerRecommendScoreAndMyRecommendTypeDto update(AnswerRecommendUpdateDto answerRecommendUpdateDto) {

        Long answerId = answerRecommendUpdateDto.answerId();
        Long userId = answerRecommendUpdateDto.userId();
        RecommendType requestedRecommendType = answerRecommendUpdateDto.recommendType();

        if (requestedRecommendType == RecommendType.NONE) {
            deleteAnswerRecommendPort.deleteByAnswerIdAndUserId(answerId, userId);
        } else {
            upsertAnswerRecommendPort.upsert(answerId, userId, requestedRecommendType.name());
        }

        Integer recommendScore = loadAnswerRecommendPort.findRecommendScoreByAnswerId(answerId);

        return AnswerRecommendScoreAndMyRecommendTypeDto.of(answerId, recommendScore, requestedRecommendType);
    }
}
