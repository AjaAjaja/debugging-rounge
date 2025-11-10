package ajaajaja.debugging_rounge.feature.question.recommend.application;

import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.in.UpdateQuestionRecommendUseCase;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.*;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionRecommendFacade implements UpdateQuestionRecommendUseCase {

    private final LoadQuestionRecommendScorePort loadQuestionRecommendScorePort;
    private final DeleteQuestionRecommendPort deleteQuestionRecommendPort;
    private final UpsertQuestionRecommendPort upsertQuestionRecommendPort;

    @Override
    @Transactional
    public QuestionRecommendScoreAndMyRecommendTypeDto update(QuestionRecommendUpdateDto questionRecommendUpdateDto) {

        Long questionId = questionRecommendUpdateDto.questionId();
        Long userId = questionRecommendUpdateDto.userId();
        RecommendType requestedRecommendType = questionRecommendUpdateDto.recommendType();

        if (requestedRecommendType == RecommendType.NONE) {
            deleteQuestionRecommendPort.deleteByQuestionIdAndUserId(questionId, userId);
        } else {
            upsertQuestionRecommendPort.upsert(questionId, userId, requestedRecommendType.name());
        }

        Integer recommendScore = loadQuestionRecommendScorePort.findRecommendScoreByQuestionId(questionId);

        return QuestionRecommendScoreAndMyRecommendTypeDto.of(recommendScore, requestedRecommendType);
    }
}
